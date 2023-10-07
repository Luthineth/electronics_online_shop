package com.store.Online.Store.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class CategoryRequest {

    private Integer parentCategoryId;

    private Integer categoryId;

    private String nameCategory;

    public CategoryRequest(Integer parentCategoryId, Integer categoryId, String nameCategory) {
        this.parentCategoryId = parentCategoryId;
        this.categoryId = categoryId;
        this.nameCategory = nameCategory;
    }
}
