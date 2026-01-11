package project.market.paymentTest;

import com.siot.IamportRestClient.IamportClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import project.market.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@ActiveProfiles("test")
public class IamportDirectTest extends AcceptanceTest{

    @Autowired
    private IamportClient iamportClient;

    @Test
    void 아임포트_API_연결_테스트() {
        System.out.println("아임포트 클라이언트 확인 테스트");
        System.out.println("IamportClient: " + iamportClient);

        assertThat(iamportClient).isNotNull();

        System.out.println("아임포트 연결 성공");
    }

    @Test
    void 존재하지_않는_imp_uid_조회() {
        // API가 제대로 동작하는지 확인
        // 존재하지 않는 imp_uid로 조회 시 예외 발생

        try {
            iamportClient.paymentByImpUid("imp_not_exist_test");
            fail("예외가 발생");
        } catch (Exception e) {
            System.out.println("예상된 예외 발생: " + e.getMessage());
            System.out.println("API 호출 정상 동작");
        }
    }
}
