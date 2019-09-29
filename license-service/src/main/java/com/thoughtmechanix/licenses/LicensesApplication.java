package com.thoughtmechanix.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

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
public class LicensesApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicensesApplication.class, args);
	}

}
