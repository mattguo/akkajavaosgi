package com.mattguo.playakka;

import com.mattguo.playakka.Messages.Greet;
import com.mattguo.playakka.Messages.Greeting;
import com.mattguo.playakka.Messages.WhoToGreet;

import akka.actor.UntypedActor;

public class Greeter extends UntypedActor {
    String greeting = "";

    @Override
    public void onReceive(Object message) {
        if (message instanceof WhoToGreet)
            greeting = "hello, " + ((WhoToGreet) message).who;

        else if (message instanceof Greet)
            // Send the current greeting back to the sender
            getSender().tell(new Greeting(greeting), getSelf());

        else unhandled(message);
    }
}
