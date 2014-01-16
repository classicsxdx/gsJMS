/*Spring JMS tutorial
 * This guide walks you through the process of publishing and subscribing to messages using a JMS broker.
 * This tut requires knowledge of JMS
http://spring.io/guides/gs/messaging-jms/

	Jan 15, 2014*/

package hello;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.util.FileSystemUtils;

import java.io.File;


@Configuration
@EnableAutoConfiguration
public class Application {
	
	// This var is where your msgs will reside
	static String mailboxDestination = "mailbox destination";
	
/*	Instantiate a new Receiver and call it receiver.
	Creates a copy of the applciation context*/
	
	@Bean
	Receiver receiver() {
		return new Receiver();
	}
	
	/*To wrap the Receiver you coded earlier, use MessageListenerAdapter. 
	Then use the setDefaultListenerMethod to configure which method to 
	invoke when a message comes in. Thus you avoid implementing any JMS 
	or broker-specific interfaces.*/
	
		@Bean
		MessageListenerAdapter adapter(Receiver receiver) {
			return new MessageListenerAdapter(receiver) {
				{
					setDefaultListenerMethod("receiveMessage");
				}
			};
		}
			
/*		
		The SimpleMessageListenerContainer class is an asynchronous message 
		receiver. It uses the MessageListenerAdapter and the ConnectionFactory 
		and is fired up when the application context starts. Another parameter 
		is the queue name set in mailboxDestination. It is also set up to receive 
		messages in a publish/subscribe fashion.*/
		
		@Bean
		SimpleMessageListenerContainer container(final MessageListenerAdapter messageListener,
				final ConnectionFactory connectionFactory) {
			return new SimpleMessageListenerContainer() {
				{
					setMessageListener(messageListener);
					setConnectionFactory(connectionFactory);
					setDestinationName(mailboxDestination);
	                setPubSubDomain(true);
				}
			};
		}

/*	Spring provides a convenient template class called JmsTemplate. JmsTemplate 
	makes it very simple to send messages to a JMS message queue. In the main 
	runner method, after starting things up, you create a MessageCreator and use 
	it from jmsTemplate to send a message.

	Two beans that you don’t see defined are JmsTemplate and ActiveMQConnectionFactory. 
	These are created automatically by Spring Boot. In this case, the ActiveMQ broker runs embedded.*/


	public static void main(String[] args) {
		// Clean out any ActiveMQ data from original run
		FileSystemUtils.deleteRecursively(new File("activemq-data"));
		
		// Launch application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        
        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("ping!");
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new message.");
        jmsTemplate.send(mailboxDestination, messageCreator);
	}

}
