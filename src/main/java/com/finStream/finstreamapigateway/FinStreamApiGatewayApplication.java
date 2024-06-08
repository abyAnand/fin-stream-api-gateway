package com.finStream.finstreamapigateway;


import com.finStream.finstreamapigateway.filters.AuthenticationFilter;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class FinStreamApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinStreamApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator finStreamRouteConfig(RouteLocatorBuilder routeLocatorBuilder, AuthenticationFilter authFilter){
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/finStream/user/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/user/(?<segment>.*)",
														"/${segment}")
												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("user-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
													.setMethods(HttpMethod.GET)
													.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
										)
								.uri("lb://USER-SERVICE")
				)
				.route(p -> p
								.path("/finStream/bank/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/bank/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("bank-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://BANK-SERVICE")
				)
				.route(p -> p
								.path("/finStream/account/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/account/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("account-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://ACCOUNT-SERVICE")
				)
				.route(p -> p
								.path("/finStream/loan/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/loan/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("loan-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://LOAN-SERVICE")
				)
				.route(p -> p
								.path("/finStream/auth/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/auth/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("auth-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://AUTH-SERVICE")
				)
				.route(p -> p
								.path("/finStream/transaction/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/transaction/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("transaction-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://TRANSACTION-SERVICE")
				)
				.route(p -> p
								.path("/finStream/notification/**")
								.filters(f -> f
												.filter(authFilter.apply(new AuthenticationFilter.Config()))
												.rewritePath("/finStream/notification/(?<segment>.*)",
														"/${segment}")

												.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
												.circuitBreaker(config -> config.setName("notification-CircuitBreaker")
														.setFallbackUri("forward:/contactSupport"))
												.retry(retryConfig -> retryConfig
														.setRetries(3)
														.setMethods(HttpMethod.GET)
														.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
												)
										.requestRateLimiter(
												config -> config.setRateLimiter(redisRateLimiter())
														.setKeyResolver(userKeyResolver())
										)
								)
								.uri("lb://NOTIFICATION-SERVICE")
				)
				.route(p -> p
						.path("/finStream/admin/**")
						.filters(f -> f
								.filter(authFilter.apply(new AuthenticationFilter.Config()))
								.rewritePath("/finStream/admin/(?<segment>.*)",
										"/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("admin-CircuitBreaker")
										.setFallbackUri("forward:/contactSupport"))
								.retry(retryConfig -> retryConfig
										.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2,true)
								)
								.requestRateLimiter(
										config -> config.setRateLimiter(redisRateLimiter())
												.setKeyResolver(userKeyResolver())
								)
						)
						.uri("lb://ADMIN-SERVICE")
				)
				.build();
	}

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom()
								.timeoutDuration(
										Duration.ofSeconds(4)
								)
						.build()
				)
				.build()
		);
	}


	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(1,1,1);
	}

	@Bean
	KeyResolver userKeyResolver(){
		return exchange -> Mono
				.justOrEmpty(
						exchange.getRequest().getHeaders().getFirst("user")
				)
				.defaultIfEmpty("anonymous");
	}

	}
