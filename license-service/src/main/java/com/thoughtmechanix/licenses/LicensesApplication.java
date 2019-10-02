package com.thoughtmechanix.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
/*
* @RefreshScope works (technically) on an @Configuration class,
* but it might lead to surprising behaviour: e.g. it does not mean that
* all the @Beans defined in that class are themselves @RefreshScope.
* Specifically, anything that depends on those beans cannot rely
* on them being updated when a refresh is initiated,
* unless it is itself in @RefreshScope (in which it will be rebuilt on a refresh
* and its dependencies re-injected, at which point they will be re-initialized from the refreshed @Configuration).
* */
@RefreshScope
/*
* trigger for Spring Cloud to enable
* the application to use the DiscoveryClient (used only in 1st client's case
* [DiscoveryClient without enhanced RestTemplate])
* (to discovery services from Eureka server)
* */
@EnableDiscoveryClient
/*
* a declarative REST client for Spring Boot apps (used only in 3rd client's case)
* (alternative for DiscoveryClient)
* */
@EnableFeignClients
public class LicensesApplication {

	public static void main(String[] args) { SpringApplication.run(LicensesApplication.class, args); }

	/*
	* Tells Spring Cloud that we want to take advantage of its load balancing support
	* (Ribbon)
	* */
	@LoadBalanced
	@Bean
	public RestTemplate getDiscoveryTemplate() {
		return new RestTemplate();
	}
}
