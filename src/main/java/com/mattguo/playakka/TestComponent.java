
package com.mattguo.playakka;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.mattguo.akka.AkkaEnv;
import com.typesafe.config.Config;

@Component(enabled = true)
public class TestComponent {
    private HelloService helloService;
    private AkkaEnv akka;

	@Reference
	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

	@Reference
	public void setAkka(AkkaEnv akka) {
        this.akka = akka;
    }

	@Activate
    public void activate(ComponentContext cc, BundleContext bc, Map<String,Object> config) {
        System.out.println("TestComponent:" + helloService.hello());

        //Testing typesafe config
        System.setProperty("simple-lib.whatever", "This value comes from a system property");

        // Load our own config values from the default location, application.conf
        Config conf = akka.config();
        System.out.println("The answer is: " + conf.getString("simple-app.answer"));
        System.out.println("The whatever is: " + conf.getString("simple-lib.whatever"));

        // In this simple app, we're allowing SimpleLibContext() to
        // use the default config in application.conf ; this is exactly
        // the same as passing in ConfigFactory.load() here, so we could
        // also write "new SimpleLibContext(conf)" and it would be the same.
        // (simple-lib is a library in this same examples/ directory).
        // The point is that SimpleLibContext defaults to ConfigFactory.load()
        // but also allows us to pass in our own Config.
//        SimpleLibContext context = new SimpleLibContext();
//        context.printSetting("simple-lib.foo");
//        context.printSetting("simple-lib.hello");
//        context.printSetting("simple-lib.whatever");
    }

	@Deactivate
    public void deactivate(int code, ComponentContext cc, BundleContext bc, Map<String,Object> config) {
	    System.out.println("TestComponent deactivated,  code:" + code);
    }
}
