package com.finStream.finstreamapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class FinStreamApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinStreamApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator finStreamRouteConfig(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/finStream/user/**")
								.filters(f -> f
										.rewritePath("/finStream/user/(?<segment>.*)",
														"/${segment}")
										.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								)
								.uri("lb://USER-SERVICE")
				)
				.route(p -> p
						.path("/finStream/bank/**")
						.filters(f -> f
								.rewritePath("/finStream/bank/(?<segment>.*)",
										"/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						)
						.uri("lb://BANK-SERVICE")
				).build();
	}

}
