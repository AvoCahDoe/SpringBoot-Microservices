package com.example.product.web;

import com.example.product.model.Product;
import com.example.product.repo.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> byId(@PathVariable Long id) {
        return repository.findById(Objects.requireNonNull(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
         Product saved = repository.save(Objects.requireNonNull(product));
         URI location = Objects.requireNonNull(URI.create("/products/" + Objects.requireNonNull(saved.getId())));
        return ResponseEntity.created(location).body(Objects.requireNonNull(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return repository.findById(Objects.requireNonNull(id))
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setWeight(product.getWeight());
                     Product updated = Objects.requireNonNull(repository.save(existing));
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(Objects.requireNonNull(id))) {
            repository.deleteById(Objects.requireNonNull(id));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

