package com.lookmarkd;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

@SpringBootApplication
@RabbitListener(queues = "asyncstats")
@EnableScheduling
public class InstagramstatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstagramstatsApplication.class, args);
	}

	@Bean
	public Queue asyncstats() {
		return new Queue("asyncstats");
	}


	@RabbitHandler
	public void process(@Payload String asyncstats) {
		System.out.println(new Date() + ": " + asyncstats);
	}

}
