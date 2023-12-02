package com.finStream.finstreamapigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * A global filter for Spring Cloud Gateway that intercepts all incoming requests.
 * It checks for the presence of a correlation ID in the request headers, used for tracing requests in a distributed system.
 * If the correlation ID is absent, it generates a new one and adds it to the request.
 */
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;


    /**
     * The main method that filters each incoming request.
     * @param exchange Represents the HTTP request-response exchange.
     * @param chain Provides a way to delegate to the next filter.
     * @return Mono<Void> to indicate when request processing is complete.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("finStream-correlation-id found in RequestTraceFilter : {}",
                    filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
            logger.debug("finStream-correlation-id generated in RequestTraceFilter : {}", correlationID);
        }
        return chain.filter(exchange);
    }


    /**
     * Checks if the correlation ID is present in the request headers.
     * @param requestHeaders The HTTP request headers.
     * @return boolean indicating the presence of correlation ID.
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null;
    }


    /**
     * Generates a new correlation ID.
     * @return String representing the unique correlation ID.
     */
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

}
