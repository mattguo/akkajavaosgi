package com.mattguo.avionics;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.avionics.message.RateChange;
import com.mattguo.avionics.message.StickBack;
import com.mattguo.avionics.message.StickForward;

public class ControlSurface extends AbstractLoggingActor {
	ActorRef altimeter;
		
	public ControlSurface(ActorRef altimeter) {
		this.altimeter = altimeter;
		
		this.receive(ReceiveBuilder.match(StickBack.class, msg -> {
			altimeter.tell(new RateChange(msg.amount), self());
		}).match(StickForward.class, msg -> {
			altimeter.tell(new RateChange(-msg.amount), self());
		}).build());
	}
}