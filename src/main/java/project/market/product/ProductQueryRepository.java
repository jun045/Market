package project.market.product;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.Brand.QBrand;
import project.market.Cate.QCategory;
import project.market.product.dto.AdminProductSearchRaw;
import project.market.product.dto.ProductSearchRaw;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QProduct qProduct = QProduct.product;
    private final QCategory qCategory = QCategory.category;
    private final QBrand qBrand = QBrand.brand;

    //상품 목록 조회
    public Page<ProductSearchRaw> findAllProducts(Pageable pageable, Long categoryId, Long brandId, String brandName, String keyword, ProductStatus productStatus){
        List<ProductSearchRaw> content = jpaQueryFactory
                .select(Projections.constructor(ProductSearchRaw.class,
                        qProduct.id,
                        qCategory.id,
                        qBrand.id,
                        qCategory.cateName,
                        qBrand.brandName,
                        qProduct.productName,
                        qProduct.listPrice,
                        qProduct.thumbnail
                ))
                .from(qProduct)
                .leftJoin(qProduct.brand, qBrand)
                .leftJoin(qProduct.category, qCategory)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        brandNameContains(brandName),
                        keywordSearch(keyword),
                        productStatusEq(productStatus),
                        qProduct.isDeleted.isFalse()
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qProduct.id.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        brandNameContains(brandName),
                        keywordSearch(keyword),
                        productStatusEq(productStatus),
                        qProduct.isDeleted.isFalse())
                .fetchOne();

        long totalProduct = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalProduct);
    }

    //관리자용 검색
    public Page<AdminProductSearchRaw> findAllProductsForAdmin(Pageable pageable, Long categoryId, Long brandId, String brandName, String keyword, ProductStatus productStatus, Boolean isDeleted){
        List<AdminProductSearchRaw> content = jpaQueryFactory
                .select(Projections.constructor(AdminProductSearchRaw.class,
                        qProduct.id,
                        qCategory.id,
                        qCategory.cateName,
                        qBrand.id,
                        qBrand.brandName,
                        qProduct.productName,
                        qProduct.listPrice,
                        qProduct.productStatus,
                        qProduct.isDeleted,
                        qProduct.thumbnail
                ))
                .from(qProduct)
                .leftJoin(qProduct.brand, qBrand)
                .leftJoin(qProduct.category, qCategory)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        brandNameContains(brandName),
                        keywordSearch(keyword),
                        productStatusEq(productStatus),
                        isDeletedEq(isDeleted)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qProduct.id.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        brandNameContains(brandName),
                        keywordSearch(keyword),
                        productStatusEq(productStatus),
                        isDeletedEq(isDeleted))
                .fetchOne();

        long totalProduct = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalProduct);

    }

    BooleanExpression categoryIdEq (Long categoryId){
        return categoryId == null ? null : qCategory.id.eq(categoryId);
    }

    BooleanExpression brandIdEq (Long brandId){
        return brandId == null ? null : qBrand.id.eq(brandId);
    }

    // 브랜드명 부분검색 (idx_brand_name_trgm 인덱스 사용)
    BooleanExpression brandNameContains (String brandName){
        return ilikeContains(qBrand.brandName, brandName);
    }

    // 키워드로 상품명 부분검색 (idx_product_name_trgm 인덱스 사용)
    BooleanExpression keywordSearch (String keyword){
        return ilikeContains(qProduct.productName, keyword);
    }

    // raw ILIKE 로 GIN trigram 인덱스를 사용하는 부분검색 조건 생성
    private BooleanExpression ilikeContains (StringPath column, String value){
        if (value == null || value.isBlank()) {
            return null;
        }
        // LIKE 와일드카드(%, _)와 이스케이프 문자(!)를 리터럴로 처리하기 위해 이스케이프
        String escaped = value.replace("!", "!!")
                              .replace("%", "!%")
                              .replace("_", "!_");
        return Expressions.booleanTemplate(
                "{0} ilike {1} escape '!'", column, "%" + escaped + "%");
    }

    BooleanExpression productStatusEq (ProductStatus status){
        return status == null ? null : qProduct.productStatus.eq(status);
    }

    BooleanExpression isDeletedEq (Boolean isDeleted){
        return isDeleted == null ? null : qProduct.isDeleted.eq(isDeleted);
    }
}
