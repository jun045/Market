package project.market.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import project.market.BaseEntity;
import project.market.member.Entity.Member;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private Member member;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder
    public Cart(Member member, List<CartItem> cartItems) {
        this.member = member;
        this.cartItems = cartItems;
    }

    //최초 상품을 담을 때 장바구니 생성
    public static Cart createCart (Member member){
        Cart cart = new Cart();
        cart.member = member;
        cart.cartItems = new ArrayList<>();
        return cart;
    }

    //담은 cartItem을 Cart에 바로 반영할 수 있는 편의 매서드
    public void addCart (CartItem cartItem){
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }
}
