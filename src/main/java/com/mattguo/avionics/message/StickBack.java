package com.mattguo.avionics.message;

import java.io.Serializable;

public class StickBack implements Serializable {
	private static final long serialVersionUID = 1L;
	public final double amount;

	public StickBack(double amount) {
		this.amount = amount;
	}
}
