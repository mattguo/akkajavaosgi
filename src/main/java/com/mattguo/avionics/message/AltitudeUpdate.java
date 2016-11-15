package com.mattguo.avionics.message;

import java.io.Serializable;

public class AltitudeUpdate implements Serializable {
	private static final long serialVersionUID = 1L;
	public final double altitude;

	public AltitudeUpdate(double altitude) {
		this.altitude = altitude;
	}
}
