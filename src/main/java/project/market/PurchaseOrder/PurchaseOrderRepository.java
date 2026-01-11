package project.market.PurchaseOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.PurchaseOrder.entity.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {
    List<PurchaseOrder> findByMemberId(Long memberId);
    Optional<PurchaseOrder> findByMerchantUid(String merchantUid);

}
