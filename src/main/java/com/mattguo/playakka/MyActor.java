package com.mattguo.playakka;

import akka.actor.AbstractLoggingActor;
import akka.japi.pf.ReceiveBuilder;

import com.mattguo.playakka.Messages.Greeting;

public class MyActor extends AbstractLoggingActor {
    public MyActor() {
    	this.receive(ReceiveBuilder.match(Greeting.class, g -> {
            log().info("I was greeted with " + g.message + " by " + this.sender().toString());
        }).build()); 
    }
}
