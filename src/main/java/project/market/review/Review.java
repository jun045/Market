package project.market.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;
import project.market.cart.entity.CartItem;
import project.market.member.Entity.Member;
import project.market.product.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    //TODO orderItem 구현 후 orderItem으로 매핑 변경
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private CartItem cartItem;

    @NotNull
    private Integer rating;

    @NotNull
    private String content;

    private Boolean isDeleted;

    @Builder
    public Review(Member member, Product product, CartItem cartItem, int rating, String content, Boolean isDeleted) {
        this.member = member;
        this.product = product;
        this.cartItem = cartItem;
        this.rating = rating;
        this.content = content;
        this.isDeleted = isDeleted;
    }
}
