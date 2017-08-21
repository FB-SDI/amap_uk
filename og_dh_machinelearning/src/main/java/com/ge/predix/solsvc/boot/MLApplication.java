package com.ge.predix.solsvc.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author  -
 */
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class,
		DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"com.ge.predix.solsvc","com.ge.predix.solsvc.machinelearning.bootstrap"})
@PropertySource("classpath:application.properties")
@EnableScheduling
public class MLApplication {

	/**
	 * @param args
	 *            -
	 */
	public static void main(String[] args) {
		SpringApplication.run(
				MLApplication.class, args);
	}

	/**
	 * @return -
	 */
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}
}
