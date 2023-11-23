package com.fortytwotalents.examples.kafka_transactions;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class ExampleKafkaTransactionsApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext context;

	private static final Logger LOG = LoggerFactory
			.getLogger(ExampleKafkaTransactionsApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(ExampleKafkaTransactionsApplication.class, args);
	}

	@Bean
	public NewTopic ordersTopic() {
		return TopicBuilder.name("orders")
				.partitions(1)
				.replicas(3)
				.build();
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("Running");
	}
}
