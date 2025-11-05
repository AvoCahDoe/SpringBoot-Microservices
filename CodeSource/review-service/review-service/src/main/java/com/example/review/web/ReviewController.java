package com.example.review.web;

import com.example.review.model.Review;
import com.example.review.repo.ReviewRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository repository;

    public ReviewController(ReviewRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Review> all(@RequestParam(value = "productId", required = false) Long productId) {
        if (productId != null) return repository.findByProductId(productId);
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> byId(@PathVariable @NonNull Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
         Review saved = repository.save(Objects.requireNonNull(review));
        URI location = URI.create("/reviews/" + saved.getId());
        return ResponseEntity.created(Objects.requireNonNull(location)).body(saved);
    }
}
