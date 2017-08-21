package com.ge.predix.oilgas.servicemax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ge.predix.oilgas.servicemax"})
public class ServiceMaxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMaxApplication.class, args);
	}
}
