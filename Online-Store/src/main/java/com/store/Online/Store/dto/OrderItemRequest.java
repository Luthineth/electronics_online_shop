package com.store.Online.Store.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    private Long productId;

    private Integer quantity;
}
