package com.mattguo.avionics.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class RegisterListener implements Serializable {
	private static final long serialVersionUID = 1L;
	public final ActorRef listener;

	public RegisterListener(ActorRef listener) {
		this.listener = listener;
	}
}
