package com.ficsolution.roster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RosterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RosterApplication.class, args);
	}

}
