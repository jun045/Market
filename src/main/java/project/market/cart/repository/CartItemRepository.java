package project.market.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.cart.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductVariantId (Long CartId, Long variantId);
}
