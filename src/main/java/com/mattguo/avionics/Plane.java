package com.mattguo.avionics;

import com.mattguo.avionics.message.AltitudeUpdate;
import com.mattguo.avionics.message.Msg;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class Plane extends AbstractLoggingActor {
	private final ActorRef altimeter;
	private final ActorRef controls;

	public Plane() {
		altimeter = context().actorOf(Props.create(Altimeter.class), "altimeter");
		controls = context().actorOf(
				Props.create(ControlSurface.class, altimeter), "controls");

		this.receive(ReceiveBuilder.match(AltitudeUpdate.class, msg -> {
			log().info("Altitude is now {}", msg.altitude);
		}).matchEquals(Msg.GiveMeControl, msg -> {
			log().info("Plane giving control of {}", controls.path());
			sender().tell(controls, self());
		}).build());
	}

	@Override
	public void preStart() {
		//altimeter.tell(new RegisterListener(self()), self());
	}
}