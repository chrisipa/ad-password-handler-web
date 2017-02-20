package de.papke.ad.password.handler.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


/**
 * Main class of scripting console application.
 */
@SpringBootApplication
public class Main extends SpringBootServletInitializer {

	/**
	 * Method for configuring spring boot application
	 *
	 * @param application
	 * @return
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Main.class);
	}

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args); //NOSONAR
	}
}