package com.mattguo.playakka;

import java.io.Serializable;

public class Messages {
	public static class Greet implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	public static class WhoToGreet implements Serializable {
		private static final long serialVersionUID = 1L;
		public final String who;

		public WhoToGreet(String who) {
			this.who = who;
		}
	}

	public static class Greeting implements Serializable {
		private static final long serialVersionUID = 1L;
		public final String message;

		public Greeting(String message) {
			this.message = message;
		}
	}
}
