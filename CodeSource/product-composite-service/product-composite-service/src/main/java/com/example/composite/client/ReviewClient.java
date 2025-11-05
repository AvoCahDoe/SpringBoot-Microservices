package com.example.composite.client;

import com.example.composite.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewClient {
    @GetMapping("/reviews")
    List<ReviewDto> getByProduct(@RequestParam("productId") Long productId);
}

