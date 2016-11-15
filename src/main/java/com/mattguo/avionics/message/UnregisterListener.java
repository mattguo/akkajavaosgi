package com.mattguo.avionics.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class UnregisterListener implements Serializable {
	private static final long serialVersionUID = 1L;
	public final ActorRef listener;

	public UnregisterListener(ActorRef listener) {
		this.listener = listener;
	}
}
