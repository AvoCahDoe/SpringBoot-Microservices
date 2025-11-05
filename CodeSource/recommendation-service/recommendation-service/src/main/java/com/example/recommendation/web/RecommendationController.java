package com.example.recommendation.web;

import com.example.recommendation.model.Recommendation;
import com.example.recommendation.repo.RecommendationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationRepository repository;

    public RecommendationController(RecommendationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Recommendation> all(@RequestParam(value = "productId", required = false) Long productId) {
        if (productId != null) return repository.findByProductId(productId);
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recommendation> byId(@PathVariable @NonNull Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recommendation> create(@Valid @RequestBody Recommendation recommendation) {
        Recommendation saved = repository.save(Objects.requireNonNull(recommendation));
         URI location = URI.create("/recommendations/" + saved.getId());
        return ResponseEntity.created(Objects.requireNonNull(location)).body(saved);
    }
}
