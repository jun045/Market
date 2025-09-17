package project.market.cart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;
import project.market.product.OptionVariant;
import project.market.product.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    @ManyToOne
    private OptionVariant optionVariant;

    @NotNull
    private int quantity;

    @Builder
    public CartItem(Cart cart, Product product, int quantity, OptionVariant optionVariant) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.optionVariant = optionVariant;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    //장바구니 상품 수량 증가
    public void increaseQuantity (int newQuantity){
        this.quantity += newQuantity;
    }

    public void setCartItemQuantity (int newQuantity){
        if(newQuantity < 1){
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다");
        }
        this.quantity = newQuantity;

    }
}
