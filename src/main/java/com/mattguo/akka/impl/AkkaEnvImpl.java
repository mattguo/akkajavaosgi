package com.mattguo.akka.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mattguo.akka.AkkaEnv;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Terminated;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

@Component(service = {AkkaEnv.class}, property = { "sysname:String=MyAkka" })
public class AkkaEnvImpl implements AkkaEnv {
    Logger logger = LoggerFactory.getLogger(AkkaEnvImpl.class);

    private ActorSystem sys;

    @Activate
    public void activate(ComponentContext cc, BundleContext bc, Map<String,Object> config) {
        String systemName = (String)config.get("sysname");
        if (systemName == null)
            systemName = "MyAkka";
        initActorSystem(systemName);
        logger.info("AkkaEnvImpl activated");
    }

    @Deactivate
    public void deactivate(int code, ComponentContext cc, BundleContext bc, Map<String,Object> config) {
        stopActorSystem();
        logger.info("AkkaEnvImpl deactivated. code:{}", code);
    }

    protected void initActorSystem(String systemName) {
        ClassLoader classLoader = ActorSystem.class.getClassLoader();
        Config config = ConfigFactory.defaultReference(classLoader);
        config.checkValid(ConfigFactory.defaultReference(classLoader), "akka");

        sys = ActorSystem.create(systemName, config, classLoader);
        logger.info("{} started.", systemName);
    }

    protected void stopActorSystem() {
        Future<Terminated> fut = sys.terminate();
        try {
            Await.result(fut, Duration.create(3, TimeUnit.SECONDS));
        } catch (Exception ex) {
            logger.error("Akka termination failed.", ex);
        }
        logger.info("Akka Terminated.");
    }

    @Override
    public ActorSystem system() {
        return this.sys;
    }

    private static FiniteDuration msDuration(int milliseconds) {
        return Duration.create(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Cancellable schedule(int initDelayMs, int intervalMs, Runnable runnable) {
        return sys.scheduler().schedule(msDuration(initDelayMs), msDuration(intervalMs), runnable, sys.dispatcher());
    }

    @Override
    public Cancellable schedule(int initDelayMs, int intervalMs, ActorRef dest, Object msg) {
        return sys.scheduler().schedule(msDuration(initDelayMs), msDuration(intervalMs), dest, msg, sys.dispatcher(),
                ActorRef.noSender());
    }

    @Override
    public Cancellable schedule(int initDelayMs, int intervalMs, ActorRef dest, Object msg, ActorRef src) {
        return sys.scheduler().schedule(msDuration(initDelayMs), msDuration(intervalMs), dest, msg, sys.dispatcher(),
                src);
    }

    @Override
    public Cancellable scheduleOnce(int delayMs, Runnable runnable) {
        return sys.scheduler().scheduleOnce(msDuration(delayMs), runnable, sys.dispatcher());
    }

    @Override
    public Cancellable scheduleOnce(int delayMs, ActorRef dest, Object msg) {
        return sys.scheduler().scheduleOnce(msDuration(delayMs), dest, msg, sys.dispatcher(), ActorRef.noSender());
    }

    @Override
    public Cancellable scheduleOnce(int delayMs, ActorRef dest, Object msg, ActorRef src) {
        return sys.scheduler().scheduleOnce(msDuration(delayMs), dest, msg, sys.dispatcher(), src);
    }
}
