package com.store.Online.Store.dto;

import com.store.Online.Store.entity.Comment;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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

    private List<CommentRequest> comments;
}
