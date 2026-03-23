package com.climb.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;

@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
public class ClimbApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClimbApiApplication.class, args);
	}

}
