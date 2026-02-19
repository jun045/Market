package project.market.cart.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.ProductVariant.QProductVariant;
import project.market.cart.dto.CartItemRaw;
import project.market.cart.dto.CartTotalRaw;
import project.market.cart.entity.QCartItem;
import project.market.product.QProduct;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartItemQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QCartItem qCartItem = QCartItem.cartItem;
    private final QProduct qProduct = QProduct.product;
    private final QProductVariant qProductVariant = QProductVariant.productVariant;

    public Page<CartItemRaw> cartItemRawList (Long memberId, Long cartId, Pageable pageable){

        int size = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        List<CartItemRaw> cartItemRaw = queryFactory
                .select(Projections.constructor(CartItemRaw.class,
                        qCartItem.id,
                        qProduct.id,
                        qProduct.productName,
                        qProduct.thumbnail,
                        qCartItem.productVariant.id,
                        qCartItem.productVariant.options,
                        qCartItem.quantity,
                        qCartItem.product.listPrice,
                        qCartItem.productVariant.extraCharge,
                        qCartItem.productVariant.discountPrice,
                        qCartItem.createdAt,
                        qCartItem.updatedAt))
                .from(qCartItem)
                .join(qCartItem.product, qProduct)
                .join(qCartItem.productVariant, qProductVariant)
                .where(qCartItem.cart.member.id.eq(memberId),
                        qCartItem.cart.id.eq(cartId))
                .orderBy(qCartItem.createdAt.desc())
                .offset((long) size * pageNumber)
                .limit(size)
                .fetch();

        Long count = queryFactory
                .select(qCartItem.count())
                .from(qCartItem)
                .where(qCartItem.cart.member.id.eq(memberId),
                        qCartItem.cart.id.eq(cartId))
                .fetchOne();

        long totalItems = count != null ? count : 0L;

        return new PageImpl<>(cartItemRaw, pageable, totalItems);

    }

    //장바구니 총액 계산
    public CartTotalRaw cartTotals (Long cartId, Long memberId){

        NumberPath<Integer> quantity = qCartItem.quantity;
        NumberExpression<Integer> listPrice = qProduct.listPrice.coalesce(0);
        NumberExpression<Integer> extraCharge = qProductVariant.extraCharge.coalesce(0);
        NumberExpression<Long> discountPrice = qProductVariant.discountPrice.coalesce(0L).longValue();

        NumberExpression<Integer> unitPrice = listPrice.add(extraCharge).subtract(discountPrice);
        NumberExpression<Integer> totalItemPrice = unitPrice.multiply(quantity);

        Tuple tuple = queryFactory
                .select(totalItemPrice.sum().coalesce(0),
                        qCartItem.quantity.sum().coalesce(0)
                ).from(qCartItem)
                .join(qCartItem.product, qProduct)
                .join(qCartItem.productVariant, qProductVariant)
                .where(qCartItem.cart.id.eq(cartId),
                        qCartItem.cart.member.id.eq(memberId))
                .fetchOne();

        Number totalAmountNum = tuple != null ? tuple.get(0, Number.class) : 0L;
        Number totalQuantityNum = tuple != null ? tuple.get(1, Number.class) : 0;

        long totalAmount = totalAmountNum != null ? totalAmountNum.longValue() : 0L;
        int totalQuantity = totalQuantityNum != null ? totalQuantityNum.intValue() : 0;

        return new CartTotalRaw(totalAmount, totalQuantity);

    }

}
