package com.NMCNPM.ABT_bio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbtBioApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbtBioApplication.class, args);
	}

}
