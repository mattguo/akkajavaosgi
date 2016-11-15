package com.mattguo.avionics.message;

import java.io.Serializable;

public class RateChange implements Serializable {
	private static final long serialVersionUID = 1L;
	public final double amount;

	public RateChange(double amount) {
		this.amount = amount;
	}
}
