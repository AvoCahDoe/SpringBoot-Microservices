package com.example.product;

import com.example.product.model.Product;
import com.example.product.repo.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner data(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Product("Sample Phone", 0.2));
                repo.save(new Product("Gaming Laptop", 2.5));
            }
        };
    }
}

