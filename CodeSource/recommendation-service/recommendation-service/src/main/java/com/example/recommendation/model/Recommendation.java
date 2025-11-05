package com.example.recommendation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // recommendationId

    @NotNull
    private Long productId;

    @NotBlank
    private String author;

    // Rate as percentage (0-100)
    @NotNull
    @Min(0)
    @Max(100)
    private Integer rate;

    @NotBlank
    private String content;

    public Recommendation() {}

    public Recommendation(Long productId, String author, Integer rate, String content) {
        this.productId = productId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Integer getRate() { return rate; }
    public void setRate(Integer rate) { this.rate = rate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
