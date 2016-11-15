package com.mattguo.avionics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.avionics.message.RegisterListener;
import com.mattguo.avionics.message.UnregisterListener;

public abstract class EventSource extends AbstractLoggingActor {
	private Set<ActorRef> listeners = ConcurrentHashMap.newKeySet();

	PartialFunction<Object, BoxedUnit> eventSourceReceive = ReceiveBuilder.match(RegisterListener.class, msg -> {
		listeners.add(msg.listener);
	}).match(UnregisterListener.class, msg -> {
		listeners.remove(msg.listener);
	}).build();
	
	public <T> void sendEvent(T event) {
		listeners.forEach(actor -> {
			actor.tell(event, self());
		});
	}
}
