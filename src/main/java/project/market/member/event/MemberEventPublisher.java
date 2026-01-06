package project.market.member.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void memberPublishFailedEvent (Long memberId, Long purchaseOrderId, EventType eventType, String reason){

    }
}
