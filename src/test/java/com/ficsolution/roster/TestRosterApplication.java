package com.ficsolution.roster;

import org.springframework.boot.SpringApplication;

public class TestRosterApplication {

	public static void main(String[] args) {
		SpringApplication.from(RosterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
