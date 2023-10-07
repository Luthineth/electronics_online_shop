package com.store.Online.Store.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String productName;

    private String description;

    private int stockQuantity;

    private BigDecimal price;

    private BigDecimal priceWithDiscount;

    private String imageUrl;
}
