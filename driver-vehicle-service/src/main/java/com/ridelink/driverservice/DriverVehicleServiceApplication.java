package com.ridelink.driverservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.io.File;

@SpringBootApplication
public class DriverVehicleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverVehicleServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner debugPrint(MongoTemplate mongoTemplate, Environment env) {
		return args -> {
			System.out.println(">>> DATABASE_URL property = " + env.getProperty("DATABASE_URL"));
			System.out.println(">>> spring.data.mongodb.uri property = " + env.getProperty("spring.data.mongodb.uri"));
			System.out.println(">>> spring.data.mongodb.database property = " + env.getProperty("spring.data.mongodb.database"));
			System.out.println(">>> Connected to Mongo database: " + mongoTemplate.getDb().getName());
		};
	}
}