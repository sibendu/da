package coms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableJms
public class JmsConfig {
	
	@Value( "${INPUT_QUEUE}" )
	private String inputQueue;
	
	@Value( "${ASSET_QUEUE}" )
	private String assetQueue;
	
	@Value( "${OUTPUT_QUEUE}" )
	private String outputQueue;
	
	@Bean
    public Queue input_queue(){
        return new ActiveMQQueue(inputQueue);
    }
	
	@Bean
    public Queue asset_queue(){
        return new ActiveMQQueue(assetQueue);
    }
	
	@Bean
    public Queue output_queue(){
        return new ActiveMQQueue(outputQueue);
    }
}
