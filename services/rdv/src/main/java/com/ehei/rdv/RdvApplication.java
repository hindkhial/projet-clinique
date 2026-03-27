package com.ehei.rdv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RdvApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdvApplication.class, args);
	}

}
