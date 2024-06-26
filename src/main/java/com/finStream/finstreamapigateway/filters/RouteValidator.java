package com.finStream.finstreamapigateway.filters;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/finStream/auth/auth/register",
            "/finStream/auth/auth/authenticate",
            "/finStream/auth/auth/verification/email",
            "/finStream/auth/auth/verification/otp",
            "/finStream/auth/auth/reset/password",
            "/finStream/auth/auth/validate",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
