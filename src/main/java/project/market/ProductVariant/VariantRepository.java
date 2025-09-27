package project.market.ProductVariant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository extends JpaRepository<ProductVariant,Long> {
    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdAndIsDeletedFalse(Long productId);
}
