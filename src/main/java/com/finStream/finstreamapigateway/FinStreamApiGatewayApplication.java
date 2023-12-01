package com.finStream.finstreamapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
										.rewritePath("/finStream/(?<segment>.*)",
														"/${segment}")
								)
								.uri("lb://USER-SERVICE")
				)
				.route(p -> p
						.path("/finStream/bank/**")
						.filters(f -> f
								.rewritePath("/finStream/(?<segment>.*)",
										"/${segment}")
						)
						.uri("lb://BANK-SERVICE")
				).build();
	}

}
