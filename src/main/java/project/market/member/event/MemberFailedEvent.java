package project.market.member.event;

import java.time.LocalDateTime;

public record MemberFailedEvent(Long memberId,
                                Long purchaseOrderId,
                                EventType eventType,
                                String reason,
                                LocalDateTime occurredAt) {
}
