package com.finStream.finstreamapigateway.filters;

import com.finStream.finstreamapigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (validator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return unauthorizedResponse(exchange.getResponse());
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                    try {
                        List<SimpleGrantedAuthority> roles = jwtUtil.getRolesFromToken(authHeader);

                        if (!isAuthorizedForRoute(request, roles)) {
                            return forbiddenResponse(exchange.getResponse());
                        }

                    } catch (Exception e) {
                        return forbiddenResponse(exchange.getResponse());
                    }
                }
            }
            return chain.filter(exchange);
        };
    }

    private boolean isAuthorizedForRoute(ServerHttpRequest request, List<SimpleGrantedAuthority> roles) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        boolean isAdmin = roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isBank = roles.contains(new SimpleGrantedAuthority("ROLE_BANK"));
        boolean isUser = roles.contains(new SimpleGrantedAuthority("ROLE_USER"));
        boolean isGuestAdmin = roles.contains(new SimpleGrantedAuthority("ROLE_GUEST_ADMIN"));

        if (isAdmin) {
            return true; // Admin can access anything
        }
        if (isBank && path.startsWith("/finStream/bank")) {
            return true; // Bank role can access /bank
        }
        if (isUser && path.startsWith("/finStream/user")) {
            return true; // User role can access /user
        }
        if (isGuestAdmin) {
            return method == HttpMethod.GET; // Guest Admin can access everything but only with GET
        }
        return false; // Default deny
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private Mono<Void> forbiddenResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public static class Config {

    }
}
