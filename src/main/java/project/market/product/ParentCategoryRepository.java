package project.market.product;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.member.enums.Level;

public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long> {
}
