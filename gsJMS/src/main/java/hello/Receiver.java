package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileSystemUtils;

import java.io.File;

public class Receiver {
	
	/*Get a copy of the application context*/
	
	@Autowired
	ConfigurableApplicationContext context;
	
	/*When you reveive a msg, print it out, then shut down the app.
	Clean up any ActiveMQ stuff*/
	
	/*What is activeMQ? http://activemq.apache.org/
	Seems to be a very popular Messaging software*/
		
	
	public void receiveMessage(String message) {
		System.out.println("Received <" + message +">");
		context.close();
		FileSystemUtils.deleteRecursively(new File("activemq-data"));
	}
	

}
