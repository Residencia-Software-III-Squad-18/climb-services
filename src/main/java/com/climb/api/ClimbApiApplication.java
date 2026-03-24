package com.climb.api;

import me.paulschwarz.springdotenv.spring.DotenvApplicationInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;

@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
public class ClimbApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ClimbApiApplication.class);
		app.addInitializers(new DotenvApplicationInitializer());
		app.run(args);
	}

}
