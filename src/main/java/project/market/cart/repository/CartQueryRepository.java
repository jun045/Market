package project.market.cart.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.market.cart.dto.CartRaw;
import project.market.cart.entity.QCart;
import project.market.cart.entity.QCartItem;

@Repository
@RequiredArgsConstructor
public class CartQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QCart qCart = QCart.cart;
    private final QCartItem qCartItem = QCartItem.cartItem;

    public CartRaw cartInfo (Long memberId){

        return queryFactory
                .select(Projections.constructor(CartRaw.class,
                        qCart.id,
                        qCart.updatedAt))
                .from(qCart)
                .where(qCart.member.id.eq(memberId))
                .fetchOne();
    }


}
