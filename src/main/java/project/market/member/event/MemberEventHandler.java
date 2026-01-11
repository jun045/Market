package project.market.member.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberEventHandler {

    private final EventLogRepository eventLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void memberHandler (MemberFailedEvent event){
        EventLog log = EventLog.builder()
                .eventType(event.eventType())
                .memberId(event.memberId())
                .purchaseOrderId(event.purchaseOrderId())
                .reason(event.reason())
                .occurredAt(event.occurredAt())
                .isResolved(false)
                .build();

        eventLogRepository.save(log);
    }
}
