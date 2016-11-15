
package com.mattguo.playakka;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(enabled = false)
public class TestComponent {
    private HelloService helloService;

	@Reference
	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

	@Activate
    public void activate(ComponentContext cc, BundleContext bc, Map<String,Object> config) {
        System.out.println("TestComponent:" + helloService.hello());
    }

	@Deactivate
    public void deactivate(int code, ComponentContext cc, BundleContext bc, Map<String,Object> config) {
	    System.out.println("TestComponent deactivated,  code:" + code);
    }
}
