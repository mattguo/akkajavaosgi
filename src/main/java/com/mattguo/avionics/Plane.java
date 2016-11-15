package com.mattguo.avionics;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.avionics.message.Msg;

public class Plane extends AbstractLoggingActor {
		
	public Plane() {
		ActorRef altimeter = context().actorOf(Props.create(Altimeter.class));
		ActorRef controls = context().actorOf(Props.create(ControlSurface.class, altimeter));
		
		this.receive(ReceiveBuilder.matchEquals(Msg.GiveMeControl, msg -> {
			log().info("Plane giving control.");
			sender().tell(controls, self());
		}).build());
	}
}