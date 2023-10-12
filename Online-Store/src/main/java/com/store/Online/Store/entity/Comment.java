package com.store.Online.Store.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "image_url")
    private String imageUrl;

    public Comment(){
    }
    public Comment(User userId, Product productId, String text, Integer rating, String imageUrl) {
        this.userId = userId;
        this.productId = productId;
        this.text = text;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
