package project.market.payment;

import com.siot.IamportRestClient.response.IamportResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentVerifier {

    public void pgResponseVerifier (IamportResponse<com.siot.IamportRestClient.response.Payment> pgResponse, String merchantUid, BigDecimal fixedAmount){

        var pg = pgResponse.getResponse();

        if(!pg.getMerchantUid().equals(merchantUid)){
            throw new IllegalStateException("주문 번호가 일치하지 않습니다.");
        }

        if(pg.getAmount().compareTo(fixedAmount) != 0){
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

    }
}
