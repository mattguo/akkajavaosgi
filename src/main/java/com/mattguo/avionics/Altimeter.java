package com.mattguo.avionics;

import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.avionics.message.RateChange;

public class Altimeter extends AbstractLoggingActor {
	final static Object tickMsg = new Object(); 
	
	// The maximum ceiling of our plane in 'feet'
	final int ceiling = 43000;
	// The maximum rate of climb for our plane in 'feet per minute'
	final int maxRateOfClimb = 5000;
	// The varying rate of climb depending on the movement of the stick
	double rateOfClimb = 0;
	// Our current altitude
	double altitude = 0;

	Temporal lastTick = now();
	
	public Altimeter() {
		final ActorSystem system = this.context().system();
		ExecutionContext defaultExec = system.dispatcher();
		system.scheduler().schedule(Duration.Zero(), Duration.create(3000, TimeUnit.MILLISECONDS), self(), tickMsg,
				defaultExec, ActorRef.noSender());
		
		this.receive(ReceiveBuilder.match(RateChange.class, msg -> {
			rateOfClimb = Math.max(Math.min(msg.amount, 1.0), -1.0) * maxRateOfClimb;
			log().info("Altimeter changed rate of climb to {}", rateOfClimb);
		}).matchEquals(tickMsg, msg -> {
			Temporal tick = now();
			long millis = ChronoUnit.MILLIS.between(lastTick,  tick);
			altitude = altitude + rateOfClimb * millis / 60000;
			lastTick = tick;
			log().info("Altimeter ontick {}, altitute:{}", tick, altitude);
		}).build());
	}
	
	private Temporal now() {
		return OffsetTime.now();
		
	}
}