package project.market.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.cart.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberId(Long memberId);
}
