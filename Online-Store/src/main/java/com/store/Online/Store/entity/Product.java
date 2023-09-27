package com.store.Online.Store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false,unique = true,length = 255)
    private String productName;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal priceWithDiscount;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discount_id")
    private Discount productDiscount;

    @OneToMany(mappedBy = "productId")
    private Collection<OrderItem> orderItems;

    @JsonIgnore
    @OneToMany(mappedBy = "productId")
    private Collection<ProductCategory> productCategoryCollection;

    @JsonIgnore
    @OneToMany(mappedBy = "productId")
    private Collection<Comment> comments;
    public Product(){
    }
}
