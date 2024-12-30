package com.example.techpuram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enable scheduling
public class TechpuramApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechpuramApplication.class, args);
	}

}
