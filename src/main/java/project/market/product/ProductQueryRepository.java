package project.market.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.Brand.QBrand;
import project.market.Cate.QCategory;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QProduct qProduct = QProduct.product;
    private final QCategory qCategory = QCategory.category;
    private final QBrand qBrand = QBrand.brand;

    //상품 목록 조회
    public Page<ProductSearchResponse> findAllProducts(Pageable pageable){
        List<ProductSearchResponse> content = jpaQueryFactory
                .select(Projections.constructor(ProductSearchResponse.class,
                        qProduct.id,
                        qCategory.cateName,
                        qBrand.brandName,
                        qProduct.productName,
                        qProduct.listPrice
                       ))
                .from(qProduct)
                .leftJoin(qProduct.brand, qBrand)
                .leftJoin(qProduct.category, qCategory)
                .where(qProduct.isDeleted.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qProduct.id.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(qProduct.isDeleted.eq(false))
                .fetchOne();

        //pageable 정렬 정보 자동 처리
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
