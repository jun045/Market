package project.market.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;
import project.market.member.Entity.Member;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder
    public Cart(Member member, List<CartItem> cartItems) {
        this.member = member;
        this.cartItems = (cartItems != null) ? cartItems : new ArrayList<>();
    }

    //최초 상품을 담을 때 장바구니 생성
    public static Cart createCart (Member member){
        Cart cart = new Cart();
        cart.member = member;
        cart.cartItems = new ArrayList<>();
        return cart;
    }

    //CartItem을 Cart에 추가하는 양방향 참조 매서드
    public void addCart (CartItem cartItem){
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }


}
