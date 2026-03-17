package project.market.PurchaseOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.market.PurchaseOrder.entity.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {
    List<PurchaseOrder> findByMemberId(Long memberId);
    Optional<PurchaseOrder> findByMerchantUid(String merchantUid);

    //사용자용 - 삭제된 주문 조회 불가(isDeleted=false 조건)
    @Query("SELECT DISTINCT o FROM PurchaseOrder o " +
            "LEFT JOIN FETCH o.member " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.productVariant pv " +
            "LEFT JOIN FETCH pv.product " +
            "WHERE o.member.id = :memberId " +
            "AND o.isDeleted = false")
    List<PurchaseOrder> findAllWithDetailsByMemberId(@Param("memberId") Long memberId);
}
