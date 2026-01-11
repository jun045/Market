package project.market.member.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Long purchaseOrderId;

    private Long memberId;

    private String reason;

    private boolean isResolved;

    private LocalDateTime occurredAt;

    @Builder
    public EventLog(EventType eventType, Long purchaseOrderId, Long memberId, String reason, boolean isResolved, LocalDateTime occurredAt) {
        this.eventType = eventType;
        this.purchaseOrderId = purchaseOrderId;
        this.memberId = memberId;
        this.reason = reason;
        this.isResolved = isResolved;
        this.occurredAt = occurredAt;
    }
}
