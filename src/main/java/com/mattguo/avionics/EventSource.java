package com.mattguo.avionics;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public interface EventSource {
	PartialFunction<Object, BoxedUnit> eventSourceReceive();
	<T> void sendEvent(T event);
}
