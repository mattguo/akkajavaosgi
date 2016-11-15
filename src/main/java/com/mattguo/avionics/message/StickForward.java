package com.mattguo.avionics.message;

import java.io.Serializable;

public class StickForward implements Serializable {
	private static final long serialVersionUID = 1L;
	public final double amount;

	public StickForward(double amount) {
		this.amount = amount;
	}
}
