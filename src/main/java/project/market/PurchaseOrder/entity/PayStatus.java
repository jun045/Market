package project.market.PurchaseOrder.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PayStatus {
    PAID("paid"),
    FAILED("failed"),
    READY("ready");

    private final String pgValue;

    public static PayStatus fromPgValue (String pgValue){
        return Arrays.stream(values())
                .filter(payStatus -> payStatus.pgValue.equalsIgnoreCase(pgValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("결제 중 오류가 발생하였습니다. 오류 코드: " + pgValue));

    }

}
