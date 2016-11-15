
package com.mattguo.playakka;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component
public class TestComponent {
    private HelloService helloService;

	@Reference
	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}
	
	@Activate
    public void activate() {
        System.out.println("TestComponent:" + helloService.hello());
    }
	
	@Deactivate
    public void deactivate() {
        System.out.println("TestComponent deactivated");
    }
}
