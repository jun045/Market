package project.market.cart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.BaseEntity;
import project.market.ProductVariant.ProductVariant;
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
    private ProductVariant productVariant;

    @NotNull
    private int quantity;

    @Builder
    public CartItem(Cart cart, Product product, ProductVariant productVariant, int quantity) {
        this.cart = cart;
        this.product = product;
        this.productVariant = productVariant;
        this.quantity = quantity;
    }

    public void increaseQuantity (int newQuantity){
        this.quantity += newQuantity;
    }

    public void setCart (Cart cart){
        this.cart = cart;
    }

    public void setCartItemQuantity(int quantity) {
        this.quantity = quantity;
    }
}
