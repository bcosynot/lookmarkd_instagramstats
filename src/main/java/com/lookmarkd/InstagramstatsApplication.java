package com.lookmarkd;

import com.lookmarkd.domain.FOSUser;
import com.lookmarkd.repository.FOSUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@SpringBootApplication
@RabbitListener(queues = "asyncstats")
@EnableScheduling
@ComponentScan(basePackages = "com.lookmarkd")
public class InstagramstatsApplication {

	@Autowired
	FOSUserRepository fosUserRepository;

	private final Logger logger = LoggerFactory.getLogger(InstagramstatsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(InstagramstatsApplication.class, args);
	}

	@Bean
	public Queue asyncstats() {
		return new Queue("asyncstats");
	}


	@RabbitHandler
	public void process(@Payload String asyncstats) {
		logger.info("Message acquired:"+asyncstats);
		Map<String, Object> message = JsonParserFactory.getJsonParser().parseMap(asyncstats);
		if(message.containsKey("body") && ((Map<String, Object>)message.get("body")).containsKey("username")) {
			String username = (String) ((Map<String, Object>) message.get("body")).get("username");
			logger.info("Username:"+username);
			FOSUser user = this.fosUserRepository.findByUsername(username);
			logger.info("user acquired: " + user.getId());
		}
	}

}
