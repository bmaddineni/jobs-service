package com.oneun.jobsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobsServiceApplication.class, args);
	}

}
