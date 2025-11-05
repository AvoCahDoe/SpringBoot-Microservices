package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    public AuthorizationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip auth service endpoints to avoid recursion
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        // Skip actuator endpoints (health, info, metrics, etc.)
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Skip CORS preflight
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String username = request.getHeaders().getFirst("username");
        String password = request.getHeaders().getFirst("password");
        String role = request.getHeaders().getFirst("role");

        if (isBlank(username) || isBlank(password) || isBlank(role)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Map<String, String> body = Map.of(
                "username", Objects.requireNonNull(username),
                "password", Objects.requireNonNull(password),
                "role", Objects.requireNonNull(role)
        );

        return webClientBuilder.build()
                .post()
                .uri("http://authorization-service/auth/validate")
                .bodyValue(body)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return chain.filter(exchange);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                .onErrorResume(ex -> {
                    // If the auth service is down or errors, deny access gracefully
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public int getOrder() {
        return -1; // high precedence: run early
    }
}
