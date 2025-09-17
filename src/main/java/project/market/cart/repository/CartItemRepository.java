package project.market.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
