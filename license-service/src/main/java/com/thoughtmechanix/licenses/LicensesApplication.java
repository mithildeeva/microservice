package com.thoughtmechanix.licenses;

import com.thoughtmechanix.licenses.events.CustomStreamChannels;
import com.thoughtmechanix.licenses.model.message.OrganizationChange;
import com.thoughtmechanix.licenses.util.context.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

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
/*
* It will scan the classpath for any compatible Circuit Breaker implementation (Netflix Hystrix)
* */
@EnableCircuitBreaker
/*
* The @EnableBinding annotation tells the service
* to the use the channels defined in the Sink
* interface to listen for incoming messages.
*
* This annotation can be moved anywhere
* */
@EnableBinding(Sink.class)
/*
* For Custom Channels
*
* There can be multiple of these annotations (class-level)
* */
//@EnableBinding(CustomStreamChannels.class)
public class LicensesApplication {

	public static void main(String[] args) { SpringApplication.run(LicensesApplication.class, args); }

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

	/*
	* Tells Spring Cloud Stream to execute the
	* loggerSink() method every time a message is received off
	* the "input" channel (which is mentioned in the config)
	* */
	@StreamListener(Sink.INPUT)
	/*
	* For custom channels
	* */
//	@StreamListener("inboundOrgChanges")
	public void loggerSink(OrganizationChange change) {
		System.out.printf("Received an event for org ID: %s", change.getOrgId());
	}
}
