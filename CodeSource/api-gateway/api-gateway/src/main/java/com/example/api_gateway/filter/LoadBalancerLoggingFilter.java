package com.example.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

@Component
public class LoadBalancerLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoadBalancerLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Execute after downstream processing to get resolved host/port
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            URI resolvedRequestUrl = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            
            @SuppressWarnings("unchecked")
            Set<URI> originalUrls = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

            String original = (originalUrls != null && !originalUrls.isEmpty())
                    ? originalUrls.iterator().next().toString()
                    : exchange.getRequest().getURI().toString();

            if (resolvedRequestUrl != null && route != null) {
                log.info("Gateway forwarded '{}' via route '{}' to {}://{}:{}{}",
                        original,
                        route.getId(),
                        resolvedRequestUrl.getScheme(),
                        resolvedRequestUrl.getHost(),
                        resolvedRequestUrl.getPort(),
                        resolvedRequestUrl.getPath());
            } else {
                log.debug("Gateway forwarding info unavailable (route or resolved URL missing)");
            }
        }));
    }

    @Override
    public int getOrder() {
        // Run last to ensure the load balancer has resolved the host/port
        return Ordered.LOWEST_PRECEDENCE;
    }
}
