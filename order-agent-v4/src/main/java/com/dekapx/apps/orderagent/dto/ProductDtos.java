package com.dekapx.apps.orderagent.dto;

import java.math.BigDecimal;

public class ProductDtos {

    public record ProductSearchResult(
            Long id,
            String sku,
            String name,
            String description,
            String category,
            String brand,
            BigDecimal price,
            Integer stockQuantity,
            double similarityScore
    ) {}
}
