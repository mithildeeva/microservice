package com.thoughtmechanix.organizations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class OrganizationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrganizationsApplication.class, args);
	}

}
