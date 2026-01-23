package project.market.paymentTest;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.market.AcceptanceTest;
import project.market.Brand.Brand;
import project.market.Cate.Category;
import project.market.DataSeeder;
import project.market.DatabaseCleanup;
import project.market.OrderItem.dto.CreateOrderItemRequest;
import project.market.PerfMetrics;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.address.entity.Address;
import project.market.auth.JwtProvider;
import project.market.cart.entity.Cart;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Level;
import project.market.member.enums.Role;
import project.market.payment.dto.*;
import project.market.product.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class PaymentTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    DataSeeder dataSeeder;
    @Autowired JwtProvider jwtProvider;
    @Autowired MemberRepository memberRepository;

    @MockitoBean
    private IamportClient iamportClient;

    private Long parentId;
    private Long categoryId;
    private Long brandId;
    private String adminToken;
    private String userToken;
    private Long cartItemId1;
    private Long cartItemId2;
    private Long cartItemId3;
    private Long cartItemId4;
    private Member user1;
    private String merchantUid;
    private Long orderId;
    private Long addressId1;
    private Long addressId2;
    private Long ov1;
    private Long ov2;
    private Long ov3;
    private Long ov4;
    private int payAmount;


    @BeforeEach
    public void setUp () {
        RestAssured.port = port;
        databaseCleanup.execute();

        //유저생성
        user1 = dataSeeder.createUser1();
        Long userId = user1.getId();

        //관리자생성
        Member admin = dataSeeder.createAdmin();
        Long adminId = admin.getId();

        userToken = jwtProvider.createToken(userId, Role.BUYER);
        adminToken = jwtProvider.createToken(adminId, Role.SELLER);

        Address address1 = dataSeeder.createAddress1();
        addressId1 = address1.getId();

        Address address2 = dataSeeder.createAddress2();
        addressId2 = address2.getId();

        //카테고리 생성
        Category category = dataSeeder.createCategory();
        parentId = category.getParentCategory().getId();
        categoryId = category.getId();

        //브랜드 생성
        Brand brand = dataSeeder.createBrand();
        brandId = brand.getId();

        //제품생성
        Product product = dataSeeder.createProduct1();
        Long productId = product.getId();
        ProductVariant optionVariants1 = dataSeeder.createOptionVariants1();
        ProductVariant optionVariants2 = dataSeeder.createOptionVariants2();
        ProductVariant optionVariants3 = dataSeeder.createOptionVariants3();
        ProductVariant optionVariants4 = dataSeeder.createOptionVariants4();

        ov1 = optionVariants1.getId();
        ov2 = optionVariants2.getId();
        ov3 = optionVariants3.getId();
        ov4 = optionVariants4.getId();


        //장바구니 생성
        Cart cart = dataSeeder.createCartWithCartItems();
        cartItemId1 = cart.getCartItems().get(0).getId();
        cartItemId2 = cart.getCartItems().get(1).getId();
        cartItemId3 = cart.getCartItems().get(2).getId();
        cartItemId4 = cart.getCartItems().get(3).getId();

        OrderDetailResponse order = createOrder();
        orderId = order.id();
        merchantUid = order.merchantUid();
        payAmount = order.payAmount();

    }

    @DisplayName("결제 전 검증 테스트")
    @Test
    public void 결제전검증(){

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("ordersId", orderId)
                .when()
                .post("/api/v1/payments/{ordersId}/validation")
                .then().log().all()
                .statusCode(200);

    }

    @DisplayName("결제 모킹 테스트")
    @Test
    public void 결제테스트 () throws IamportResponseException, IOException {

        //결제 의도 생성
        PaymentIntentResponse paymentIntentResponse = preparePayment();

        BigDecimal amount = paymentIntentResponse.amount();
        String merchantUidForPay = paymentIntentResponse.merchantUid();

        // Mcok PG 응답 (Payment)
        com.siot.IamportRestClient.response.Payment mockPgPayment = mock(com.siot.IamportRestClient.response.Payment.class);

        when(mockPgPayment.getImpUid()).thenReturn("imp_test_123");
        when(mockPgPayment.getMerchantUid()).thenReturn(merchantUid);
        when(mockPgPayment.getAmount()).thenReturn(BigDecimal.valueOf(100));
        when(mockPgPayment.getStatus()).thenReturn("paid");

        //Mock IamportResponse
        IamportResponse<Payment> mockResponse = mock(IamportResponse.class);

        when(mockResponse.getResponse()).thenReturn(mockPgPayment);  //mockPgPayment 반환

        //IamportClient Mock 설정
        when(iamportClient.paymentByImpUid("imp_test_123")).thenReturn(mockResponse);

        PaymentVerifyRequest request = new PaymentVerifyRequest("imp_test_123", merchantUidForPay, amount);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(request)
                .when()
                .post("/api/v1/payments/verify")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("결제 후 트랜잭션 테스트")
    @Test
    public void 트랜젝션테스트 () throws IamportResponseException, IOException {

        //결제 의도 생성
        PaymentIntentResponse paymentIntentResponse = preparePayment();

        BigDecimal amount = paymentIntentResponse.amount();
        String merchantUidForPay = paymentIntentResponse.merchantUid();

        //결제
        PaymentVerifyResponse payment = pgPayment(merchantUidForPay, amount);

        //결제 후 트랜지션
        PaymentConfirmResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new PaymentConfirmRequest("imp_test_123", merchantUid))
                .when()
                .post("/api/v1/payments/confirm")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PaymentConfirmResponse.class);

        Member refreshedMember = memberRepository.findById(user1.getId()).orElseThrow();

        assertThat(refreshedMember.getTotalSpentAmount()).isEqualTo(16000000);
        assertThat(refreshedMember.getLevel()).isEqualTo(Level.GOLD);

    }

    @DisplayName("결제 내역 조회 테스트")
    @Test
    public void 결제내역조회 () throws IamportResponseException, IOException {
        //결제 의도 생성
        PaymentIntentResponse paymentIntentResponse = preparePayment();

        BigDecimal amount = paymentIntentResponse.amount();
        String merchantUidForPay = paymentIntentResponse.merchantUid();

        //결제
        pgPayment(merchantUidForPay, amount);

        //결제 후처리
        confirmPayment();

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/api/v1/me/payments/all")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        // 조회 Query, 응답시간
        logPerfMetric(response);

    }






    //주문 생성 메서드
    private OrderDetailResponse createOrder () {

        CreateOrderItemRequest item1 = new CreateOrderItemRequest(ov1, 1);
        CreateOrderItemRequest item2 = new CreateOrderItemRequest(ov2, 2);
        CreateOrderItemRequest item3 = new CreateOrderItemRequest(ov3, 3);
        CreateOrderItemRequest item4 = new CreateOrderItemRequest(ov4, 4);
        List<CreateOrderItemRequest> items = List.of(item1, item2, item3, item4);


        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateOrderRequest(0, items))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);
    }

    //결제 전 검증 메서드
    private PaymentIntentResponse preparePayment (){

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("ordersId", orderId)
                .when()
                .post("/api/v1/payments/{ordersId}/validation")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PaymentIntentResponse.class);
    }

    //결제 진행 메서드
    private PaymentVerifyResponse pgPayment (String merchantUid, BigDecimal amount) throws IamportResponseException, IOException {

        // Mcok PG 응답 (Payment)
        com.siot.IamportRestClient.response.Payment mockPgPayment = mock(com.siot.IamportRestClient.response.Payment.class);

        when(mockPgPayment.getImpUid()).thenReturn("imp_test_123");
        when(mockPgPayment.getMerchantUid()).thenReturn(merchantUid);
        when(mockPgPayment.getAmount()).thenReturn(BigDecimal.valueOf(100));
        when(mockPgPayment.getStatus()).thenReturn("paid");

        //Mock IamportResponse
        IamportResponse<Payment> mockResponse = mock(IamportResponse.class);

        when(mockResponse.getResponse()).thenReturn(mockPgPayment);  //mockPgPayment 반환

        //IamportClient Mock 설정
        when(iamportClient.paymentByImpUid("imp_test_123")).thenReturn(mockResponse);

        PaymentVerifyRequest request = new PaymentVerifyRequest("imp_test_123", merchantUid, amount);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(request)
                .when()
                .post("/api/v1/payments/verify")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PaymentVerifyResponse.class);

    }

    //결제 후처리 메서드
    private PaymentConfirmResponse confirmPayment (){

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new PaymentConfirmRequest("imp_test_123", merchantUid))
                .when()
                .post("/api/v1/payments/confirm")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PaymentConfirmResponse.class);
    }

    //쿼리 측정
    private void logPerfMetric (Response res){
        PerfMetrics metrics = PerfMetrics.from(res);
        System.out.println(metrics.format());
    }


}
