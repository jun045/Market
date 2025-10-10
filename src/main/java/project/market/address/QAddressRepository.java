package project.market.address;

import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.market.address.entity.QAddress;

@RequiredArgsConstructor
@Repository
public class QAddressRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QAddress qAddress = QAddress.address1;

    public void clearDefault (Long memberId){
        jpaQueryFactory.update(qAddress)
                .set(qAddress.isDefaultedAddress, false)
                .where(qAddress.member.id.eq(memberId),
                qAddress.isDefaultedAddress.eq(true)
                )
                .execute();
    }

}
