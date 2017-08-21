package com.ge.predix.gas.dhassert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ge.predix"})
public class OilGasDhAssetApplication {

	public static void main(String[] args) {
		SpringApplication.run(OilGasDhAssetApplication.class, args);
	}
}
