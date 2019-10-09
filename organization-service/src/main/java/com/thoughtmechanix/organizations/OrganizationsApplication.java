package com.thoughtmechanix.organizations;

import com.thoughtmechanix.organizations.util.context.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RefreshScope
@EnableCircuitBreaker
public class OrganizationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrganizationsApplication.class, args);
	}

	/*
	* Tells Spring Cloud that we want to take advantage of its load balancing support
	* (Ribbon)
	* */
	@LoadBalanced
	@Bean
	public RestTemplate getDiscoveryTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List interceptors = restTemplate.getInterceptors();
		if (interceptors == null) {
			restTemplate.setInterceptors(
					Collections.singletonList(new UserContextInterceptor())
			);
		} else {
			interceptors.add(new UserContextInterceptor());
			restTemplate.setInterceptors(interceptors);
		}

		return restTemplate;
	}
}
