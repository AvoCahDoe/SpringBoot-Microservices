package com.example.composite.web;

import com.example.composite.client.ProductClient;
import com.example.composite.client.RecommendationClient;
import com.example.composite.client.ReviewClient;
import com.example.composite.dto.ProductAggregate;
import com.example.composite.dto.ProductDto;
import com.example.composite.dto.RecommendationDto;
import com.example.composite.dto.ReviewDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/product-composite")
public class ProductCompositeController {
    private final ProductClient productClient;
    private final ReviewClient reviewClient;
    private final RecommendationClient recommendationClient;
    private final Counter getRequestsCounter;
    private final Counter writeRequestsCounter;

    public ProductCompositeController(ProductClient productClient, ReviewClient reviewClient, RecommendationClient recommendationClient, MeterRegistry meterRegistry) {
        this.productClient = productClient;
        this.reviewClient = reviewClient;
        this.recommendationClient = recommendationClient;
        this.getRequestsCounter = Counter.builder("product_composite.requests.get")
                .description("Nombre de requêtes GET sur product-composite")
                .register(meterRegistry);
        this.writeRequestsCounter = Counter.builder("product_composite.requests.write")
                .description("Nombre de requêtes POST/PUT sur product-composite")
                .register(meterRegistry);
    }

    @GetMapping("/{productId}")
    @CircuitBreaker(name = "composite", fallbackMethod = "fallback")
    public ProductAggregate aggregate(@PathVariable @NonNull Long productId) {
        // Compteur GET
        getRequestsCounter.increment();
        ProductDto product = productClient.getById(productId);
        List<ReviewDto> reviews = reviewClient.getByProduct(productId);
        List<RecommendationDto> recommendations = recommendationClient.getByProduct(productId);
        return new ProductAggregate(product, reviews, recommendations);
    }

    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createAggregate(@PathVariable @NonNull Long productId) {
        // Compteur POST/PUT
        writeRequestsCounter.increment();
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateAggregate(@PathVariable @NonNull Long productId) {
        // Compteur POST/PUT
        writeRequestsCounter.increment();
    }

    // Fallback aggregate when any downstream call fails
    public ProductAggregate fallback(Long productId, Throwable ex) {
        ProductDto minimal = new ProductDto();
        minimal.setId(productId);
        minimal.setName("Unavailable");
        minimal.setWeight(null);
        return new ProductAggregate(minimal, Collections.emptyList(), Collections.emptyList());
    }
}
