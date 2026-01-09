package project.market.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;
import project.market.OrderItem.OrderItem;
import project.market.member.Entity.Member;
import project.market.product.Product;

import java.beans.JavaBean;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private OrderItem orderItem;

    @NotNull
    private Integer rating;

    @NotNull
    private String content;

    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Review(Member member, Product product, OrderItem orderItem, Integer rating, String content, Boolean isDeleted, LocalDateTime deletedAt) {
        this.member = member;
        this.product = product;
        this.orderItem = orderItem;
        this.rating = rating;
        this.content = content;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static Review createReview (Member member, Product product, OrderItem orderItem, Integer reviewRating, String reviewContent){
        return Review.builder()
                .member(member)
                .product(product)
                .orderItem(orderItem)
                .rating(reviewRating)
                .content(reviewContent)
                .build();
    }
}
