package com.mattguo.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;

public interface AkkaEnv {
    ActorSystem system();
    Cancellable schedule(int initDelayMs, int intervalMs, Runnable runnable);
    Cancellable schedule(int initDelayMs, int intervalMs, ActorRef dest, Object msg);
    Cancellable schedule(int initDelayMs, int intervalMs, ActorRef dest, Object msg, ActorRef src);
    Cancellable scheduleOnce(int delayMs, Runnable runnable);
    Cancellable scheduleOnce(int delayMs, ActorRef dest, Object msg);
    Cancellable scheduleOnce(int delayMs, ActorRef dest, Object msg, ActorRef src);
}
