package com.store.Online.Store.dto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    private Long parentCategoryId;

    private Long categoryId;

    private String nameCategory;

}
