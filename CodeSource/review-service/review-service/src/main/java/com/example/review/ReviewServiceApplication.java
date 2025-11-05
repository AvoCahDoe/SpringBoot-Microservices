package com.example.review;

import com.example.review.model.Review;
import com.example.review.repo.ReviewRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner data(ReviewRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Review(1L, "Alice", "Great", "Loved it"));
                repo.save(new Review(1L, "Bob", "Ok", "It works well"));
            }
        };
    }
}
