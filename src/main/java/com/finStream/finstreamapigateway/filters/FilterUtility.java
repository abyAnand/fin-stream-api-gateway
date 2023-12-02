package com.finStream.finstreamapigateway.filters;


import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;


/**
 * Utility class for common operations related to request and response headers in Spring Cloud Gateway.
 */
@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "finStream-correlation-id";


    /**
     * Retrieves the correlation ID from the request headers.
     * @param requestHeaders The HTTP request headers.
     * @return String representing the correlation ID, or null if not present.
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }


    /**
     * Adds or updates a header in the request.
     * @param exchange The current ServerWebExchange.
     * @param name The name of the header to add/update.
     * @param value The value of the header.
     * @return ServerWebExchange with the updated request.
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }


    /**
     * Adds or updates the correlation ID in the request headers.
     * @param exchange The current ServerWebExchange.
     * @param correlationId The correlation ID to be set.
     * @return ServerWebExchange with the updated request.
     */
    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

}
