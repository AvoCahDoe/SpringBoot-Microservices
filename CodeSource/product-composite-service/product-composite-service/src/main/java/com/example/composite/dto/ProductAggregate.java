package com.example.composite.dto;

import java.util.List;

public class ProductAggregate {
    private ProductDto product;
    private List<ReviewDto> reviews;
    private List<RecommendationDto> recommendations;

    public ProductAggregate() {}

    public ProductAggregate(ProductDto product, List<ReviewDto> reviews, List<RecommendationDto> recommendations) {
        this.product = product;
        this.reviews = reviews;
        this.recommendations = recommendations;
    }

    public ProductDto getProduct() { return product; }
    public void setProduct(ProductDto product) { this.product = product; }
    public List<ReviewDto> getReviews() { return reviews; }
    public void setReviews(List<ReviewDto> reviews) { this.reviews = reviews; }
    public List<RecommendationDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationDto> recommendations) { this.recommendations = recommendations; }
}

