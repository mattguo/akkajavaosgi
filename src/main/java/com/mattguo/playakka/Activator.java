/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mattguo.playakka;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.mattguo.avionics.Altimeter;
import com.mattguo.avionics.Plane;
import com.mattguo.avionics.message.Msg;
import com.mattguo.avionics.message.RateChange;
import com.mattguo.avionics.message.StickBack;
import com.mattguo.playakka.Messages.Greet;
import com.mattguo.playakka.Messages.Greeting;
import com.mattguo.playakka.Messages.WhoToGreet;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Activator implements BundleActivator {

	private ActorSystem system;

	public void start(BundleContext context) {
		System.out.println("Starting the bundle");
		initAkkaAvionics();
		System.out.println("Started the bundle");
	}

	public void stop(BundleContext context) {
		System.out.println("Stopping the bundle");
		stopAkka();
		System.out.println("Stopped the bundle");
	}

	void initAkkaAvionics() {
		ClassLoader classLoader = ActorSystem.class.getClassLoader();
		Config config = ConfigFactory.defaultReference(classLoader);
		config.checkValid(ConfigFactory.defaultReference(classLoader), "akka");

		system = ActorSystem.create("PlaneSimulation", config, classLoader);
		System.out.println("PlaneSimulation started.");
		try {
			final ActorRef plane = system.actorOf(Props.create(Plane.class));

			Timeout timeout = Timeout.apply(100, TimeUnit.MILLISECONDS);
			Future<Object> future = Patterns.ask(plane, Msg.GiveMeControl,
					timeout);
			final ActorRef cs = (ActorRef) Await.result(future,
					timeout.duration());
			system.scheduler().scheduleOnce(
					Duration.apply(200, TimeUnit.MILLISECONDS), () -> {
						cs.tell(new StickBack(0.8), ActorRef.noSender());
					}, system.dispatcher());
			system.scheduler().scheduleOnce(
					Duration.apply(1000, TimeUnit.MILLISECONDS), () -> {
						cs.tell(new StickBack(0), ActorRef.noSender());
					}, system.dispatcher());
			system.scheduler().scheduleOnce(
					Duration.apply(2000, TimeUnit.MILLISECONDS), () -> {
						cs.tell(new StickBack(0.5), ActorRef.noSender());
					}, system.dispatcher());
		} catch (Exception e) {
			System.out.println("Got a exception when running Avionics."
					+ e.toString());
			e.printStackTrace();
		}
	}

	void initAkkaGreeter() {
		try {
			ClassLoader classLoader = ActorSystem.class.getClassLoader();
			Config config = ConfigFactory.defaultReference(classLoader);
			config.checkValid(ConfigFactory.defaultReference(classLoader),
					"akka");

			// Create the 'helloakka' actor system
			system = ActorSystem.create("helloakka", config, classLoader);
			System.out.println("Akka started.");

			// Create the 'greeter' actor
			final ActorRef greeter = system.actorOf(
					Props.create(Greeter.class), "greeter");

			// Create the "actor-in-a-box"
			final Inbox inbox = Inbox.create(system);

			// Tell the 'greeter' to change its 'greeting' message
			greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());

			// Ask the 'greeter for the latest 'greeting'
			// Reply should go to the "actor-in-a-box"
			inbox.send(greeter, new Greet());

			final ActorRef myActor = system.actorOf(
					Props.create(MyActor.class), "myactor");

			myActor.tell(new Greeting("greeter's msg"), greeter);
			myActor.tell(new WhoToGreet("akka"), greeter);

			// Wait 5 seconds for the reply with the 'greeting' message
			final Greeting greeting1 = (Greeting) inbox.receive(Duration
					.create(5, TimeUnit.SECONDS));
			System.out.println("Greeting: " + greeting1.message);

			// Change the greeting and ask for it again
			greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
			inbox.send(greeter, new Greet());
			final Greeting greeting2 = (Greeting) inbox.receive(Duration
					.create(5, TimeUnit.SECONDS));
			System.out.println("Greeting: " + greeting2.message);

			// after zero seconds, send a Greet message every second to the
			// greeter with a sender of the GreetPrinter
			final ActorRef greetPrinter = system.actorOf(Props
					.create(GreetPrinter.class));

			final ActorRef altimeter = system.actorOf(Props
					.create(Altimeter.class));

			altimeter.tell(new RateChange(0.15), ActorRef.noSender());

			system.scheduler().schedule(Duration.Zero(),
					Duration.create(5, TimeUnit.SECONDS), greeter, new Greet(),
					system.dispatcher(), greetPrinter);
		} catch (TimeoutException ex) {
			System.out.println("Got a timeout waiting for reply from an actor");
			ex.printStackTrace();
		}

	}

	void stopAkka() {
		Future<Terminated> fut = system.terminate();
		try {
			Await.result(fut, Duration.create(3, TimeUnit.SECONDS));
		} catch (Exception ex) {
			System.out.println("Akka termination failed." + ex.toString());
		}
		System.out.println("Akka Terminated.");
	}
}