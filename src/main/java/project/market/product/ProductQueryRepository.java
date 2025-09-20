package project.market.product;

import ch.qos.logback.core.util.StringUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QProduct qProduct = QProduct.product;

    //조회수 증가
    public long incrementViewCount(Long productId){
        return queryFactory
                .update(qProduct)
                .set(qProduct.viewCount, qProduct.viewCount.add(1))
                .where(qProduct.id.eq(productId))
                .execute();
    }

    //제품 목록 가져오기
    public List<Product> searchAndPagingProduct (Long categoryId,
                                                               Long brandId,
                                                               String searchKeyword,
                                                               int pageNumber,
                                                               int size){
        return queryFactory
                .selectFrom(qProduct)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        searchByKeyword(searchKeyword))
                .orderBy(qProduct.viewCount.desc())
                .offset((long) pageNumber * size)
                .limit(size)
                .fetch();
    }

    //페이지네이션 totalElement 구하기
    public Long getTotalElement (Long categoryId, Long brandId, String searchKeyword){
        return queryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(categoryIdEq(categoryId),
                        brandIdEq(brandId),
                        searchByKeyword(searchKeyword))
                .fetchOne();
    }

    //키워드 검색
    private BooleanExpression searchByKeyword (String keyword){
        if (!StringUtils.hasText(keyword)){
            return null;
        }
        return qProduct.productName.containsIgnoreCase(keyword)
                .or(qProduct.brand.brandName.containsIgnoreCase(keyword));
    }

    //브랜드별 보기
    private BooleanExpression brandIdEq (Long brandId){
        return brandId == null ? null : qProduct.brand.id.eq(brandId);
    }

    //카테고리별 보기
    private BooleanExpression categoryIdEq (Long categoryId){
        return categoryId == null ? null : qProduct.category.id.eq(categoryId);
    }


}
