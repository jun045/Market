package project.market.Brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    //브랜드명으로 부분검색 + 대소문자 구분없음
    List<Brand> findByBrandNameContainingIgnoreCase (String name);

}
