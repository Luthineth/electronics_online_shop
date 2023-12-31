package com.store.Online.Store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false,unique = true)
    private String productName;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal priceWithDiscount;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discount_id")
    private Discount discountId;

    @JsonIgnore
    @OneToMany(mappedBy = "productId")
    private Collection<OrderItem> orderItems;

    @JsonIgnore
    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL)
    private Collection<ProductCategory> productCategoryCollection;

    @JsonIgnore
    @OneToMany(mappedBy = "productId")
    private Collection<Comment> comments;


    public Product(String productName, String description, int stockQuantity, BigDecimal price, BigDecimal priceWithDiscount,String imageUrl ,Discount discountId){
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.priceWithDiscount = priceWithDiscount;
        this.discountId = discountId;
        this.imageUrl=imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", price=" + price +
                ", priceWithDiscount=" + priceWithDiscount +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return Objects.equals(discountId, product.discountId) &&
               Objects.equals(stockQuantity, product.stockQuantity) &&
               Objects.equals(productId, product.productId) &&
               Objects.equals(productName, product.productName) &&
               Objects.equals(description, product.description) &&
               Objects.equals(price, product.price) &&
               Objects.equals(priceWithDiscount, product.priceWithDiscount) &&
               Objects.equals(imageUrl, product.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, description, stockQuantity, price, priceWithDiscount, imageUrl, discountId);
    }
}
