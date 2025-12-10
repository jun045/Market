package project.market.PurchaseOrder;

public enum OrderStatus {
    // 주문 생성됨 (결제 전)
    CREATED {
        @Override
        public void validateNext(OrderStatus next) {
            if (next != PAID && next != CANCEL_REQUESTED) {
                throw new IllegalStateException("CREATED 상태에서 " + next + " 상태로 전이 불가");
            }
        }
    },
    // 결제 완료
    PAID {
        @Override
        public void validateNext(OrderStatus next) {
            if (next != DELIVERED) {
                throw new IllegalStateException("SHIPPED 상태에서 " + next + " 상태로 전이 불가");
            }
        }
    },
    // 배송 시작
    SHIPPED {
        @Override
        public void validateNext(OrderStatus next) {
            if (next != DELIVERED) {
                throw new IllegalStateException("SHIPPED 상태에서 " + next + " 상태로 전이 불가");
            }
        }
    },

    // 배송 완료
    DELIVERED {
        @Override
        public void validateNext(OrderStatus next) {
            throw new IllegalStateException("DELIVERED 상태에서 상태 변경 불가");
        }
    },

    //취소 요청
    CANCEL_REQUESTED {
        @Override
        public void validateNext(OrderStatus next) {
            if (next != CANCELED) {
                throw new IllegalStateException("CANCEL_REQUESTED 상태에서 " + next + " 상태로 전이 불가");
            }
        }
    },

    // 주문 취소
    CANCELED {
        @Override
        public void validateNext(OrderStatus next) {
            throw new IllegalStateException("CANCELED 상태에서 상태 변경 불가");
        }
    };

    // 모든 enum 상수가 반드시 구현해야 하는 추상 메서드
    public abstract void validateNext(OrderStatus next);
}
