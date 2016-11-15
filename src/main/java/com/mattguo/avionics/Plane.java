package com.mattguo.avionics;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.avionics.message.AltitudeUpdate;
import com.mattguo.avionics.message.Msg;
import com.mattguo.avionics.message.RegisterListener;

public class Plane extends AbstractLoggingActor {
	private final ActorRef altimeter;
	private final ActorRef controls;

	public Plane() {
		altimeter = context().actorOf(Props.create(Altimeter.class));
		controls = context().actorOf(
				Props.create(ControlSurface.class, altimeter));

		this.receive(ReceiveBuilder.match(AltitudeUpdate.class, msg -> {
			log().info("Altitude is now {}", msg.altitude);
		}).matchEquals(Msg.GiveMeControl, msg -> {
			log().info("Plane giving control.");
			sender().tell(controls, self());
		}).build());
	}

	@Override
	public void preStart() {
		altimeter.tell(new RegisterListener(self()), self());
	}
}