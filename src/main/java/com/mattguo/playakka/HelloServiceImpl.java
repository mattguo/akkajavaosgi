package com.mattguo.playakka;

import org.osgi.service.component.annotations.Component;

@Component(service = HelloService.class)
public class HelloServiceImpl implements HelloService {
	@Override
	public String hello() {
		return "Hi, world";
	}
}