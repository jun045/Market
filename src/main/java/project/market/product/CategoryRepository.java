package project.market.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndParentCategory(Long categoryId, ParentCategory parentCategory);
}
