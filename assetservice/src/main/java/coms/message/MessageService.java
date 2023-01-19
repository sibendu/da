package coms.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class MessageService {
	
    @Autowired
    private JmsTemplate jmsTemplate;
	
    public void sendMessage(Queue queue, String json) throws Exception{
		System.out.println("Posting event message to "+ queue+" : "+json);
		jmsTemplate.send(queue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
              return session.createTextMessage(json);
            }
        });			
	}
}
