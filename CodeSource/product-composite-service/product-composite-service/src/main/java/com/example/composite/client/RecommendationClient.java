package com.example.composite.client;

import com.example.composite.dto.RecommendationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recommendation-service")
public interface RecommendationClient {
    @GetMapping("/recommendations")
    List<RecommendationDto> getByProduct(@RequestParam("productId") Long productId);
}

