package com.mattguo.avionics;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
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
		
		PartialFunction<Object, BoxedUnit> recvRules = ReceiveBuilder.match(StickBack.class, msg -> {
			altimeter.tell(new RateChange(msg.amount), self());
		}).match(StickForward.class, msg -> {
			altimeter.tell(new RateChange(-msg.amount), self());
		}).build();
		
		this.receive(recvRules);
	}
}