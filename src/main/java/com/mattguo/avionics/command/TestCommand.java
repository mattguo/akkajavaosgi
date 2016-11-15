
package com.mattguo.avionics.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.mattguo.akka.AkkaEnv;

@Command(scope = "akka", name = "test", description = "akka test")
@Service
public class TestCommand implements Action {

    @Option(name = "-o", aliases = { "--option" }, description = "An option to the command", required = false, multiValued = false)
    private String option;

    @Argument(name = "argument", description = "Argument to the command", required = false, multiValued = false)
    private String argument;

    @Reference
    private AkkaEnv akka;

    @Override
    public Object execute() throws Exception {
         System.out.println(akka.system().name() + " executing command test");
         System.out.println("Option: " + option);
         System.out.println("Argument: " + argument);
         return null;
    }
}
