package com.example.composite.dto;

public class ProductDto {
    private Long id;
    private String name;
    private Double weight;

    public ProductDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
}

