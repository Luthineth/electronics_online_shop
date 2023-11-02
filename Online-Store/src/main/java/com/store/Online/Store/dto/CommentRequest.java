package com.store.Online.Store.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    private String firstName;

    private Long productId;

    private String text;

    private Integer rating;

    private String imageUrl;

    private Long commentId;
}