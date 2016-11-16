package com.mattguo.avionics;

import java.util.Set;

import com.google.common.collect.Sets;
import com.mattguo.avionics.message.RegisterListener;
import com.mattguo.avionics.message.UnregisterListener;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ProductionEventSource implements EventSource {
    private Set<ActorRef> listeners = Sets.newHashSet();
    private final ActorRef owner;

    public ProductionEventSource(ActorRef owner) {
        this.owner = owner;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> eventSourceReceive() {
        return ReceiveBuilder.match(RegisterListener.class, msg -> {
            listeners.add(msg.listener);
        }).match(UnregisterListener.class, msg -> {
            listeners.remove(msg.listener);
        }).build();
    }

    @Override
    public <T> void sendEvent(T event) {
        listeners.forEach(actor -> {
            actor.tell(event, owner);
        });
    }
}
