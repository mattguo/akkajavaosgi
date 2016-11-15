
package com.mattguo.avionics;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.mattguo.akka.AkkaEnv;
import com.mattguo.avionics.message.Msg;
import com.mattguo.avionics.message.StickBack;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

@Component(immediate = true)
public class EntryComponent {
    private AkkaEnv akka;

	@Reference
	public void setAkkaEnv(AkkaEnv akka) {
		this.akka = akka;
	}

	@Activate
    public void activate() {
	    ActorSystem system = akka.system();
	    try {
            final ActorRef plane = system.actorOf(Props.create(Plane.class));

            Timeout timeout = Timeout.apply(100, TimeUnit.MILLISECONDS);
            Future<Object> future = Patterns.ask(plane, Msg.GiveMeControl,
                    timeout);
            final ActorRef cs = (ActorRef) Await.result(future,
                    timeout.duration());
            akka.scheduleOnce(200, cs, new StickBack(0.8));
            akka.scheduleOnce(1000, cs, new StickBack(0));
            akka.scheduleOnce(2000, cs, new StickBack(1));
            akka.scheduleOnce(3000, cs, new StickBack(0));
        } catch (Exception e) {
            System.out.println("Got a exception when running Avionics."
                    + e.toString());
            e.printStackTrace();
        }
        System.out.println("Avionics EntryComponent activated.");
    }

	@Deactivate
    public void deactivate() {
        System.out.println("Avionics EntryComponent deactivated");
    }
}
