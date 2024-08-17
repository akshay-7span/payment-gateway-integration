package com.payment_gateway_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentGatewayIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentGatewayIntegrationApplication.class, args);
		System.out.println("spring boot application started");
	}
}
