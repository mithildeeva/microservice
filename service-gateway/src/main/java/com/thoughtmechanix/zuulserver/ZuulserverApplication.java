package com.thoughtmechanix.zuulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/*
*  Zuul exposes a POST-based endpoint
* route /refresh that will cause it to reload its route configuration
* (If using config server)
* */
@SpringBootApplication
@EnableZuulProxy
public class ZuulserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulserverApplication.class, args);
	}

}
