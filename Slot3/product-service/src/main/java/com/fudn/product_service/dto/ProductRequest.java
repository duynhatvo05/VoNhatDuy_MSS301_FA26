package com.fudn.product_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequest {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}