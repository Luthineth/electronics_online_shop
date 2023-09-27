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
@Table(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "discount_percent", nullable = false)
    private BigDecimal discountPercentage;

    @JsonIgnore
    @OneToMany(mappedBy = "productDiscount")
    private Collection<Product> products;

    public Discount(){
    }
}
