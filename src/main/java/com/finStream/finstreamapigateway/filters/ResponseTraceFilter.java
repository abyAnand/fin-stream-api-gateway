package com.finStream.finstreamapigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;


/**
 * Configuration class for a global post-filter in Spring Cloud Gateway.
 * This filter adds the correlation ID to the response headers.
 */
@Configuration
public class ResponseTraceFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;


    /**
     * Bean definition for the post-global filter.
     * @return GlobalFilter that adds correlation ID to the response headers.
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterUtility.getCorrelationId(requestHeaders);
                if (correlationId != null && !exchange.getResponse().getHeaders().containsKey(FilterUtility.CORRELATION_ID)) {
                    logger.debug("Updated the correlation id to the outbound headers: {}", correlationId);
                    exchange.getResponse().getHeaders().add(FilterUtility.CORRELATION_ID, correlationId);
                }
            }));
        };
    }
}