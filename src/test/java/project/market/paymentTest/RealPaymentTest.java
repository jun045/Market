package project.market.paymentTest;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import project.market.AcceptanceTest;
import project.market.Brand.Brand;
import project.market.Cate.Category;
import project.market.DataSeeder;
import project.market.DatabaseCleanup;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.OrderStatus;
import project.market.PurchaseOrder.PurchaseOrderRepository;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.address.entity.Address;
import project.market.auth.JwtProvider;
import project.market.cart.entity.Cart;
import project.market.member.Entity.Member;
import project.market.member.enums.Role;
import project.market.payment.dto.PaymentIntentResponse;
import project.market.payment.dto.PaymentVerifyRequest;
import project.market.product.Product;

import java.io.IOException;
import java.math.BigDecimal;

@ActiveProfiles("test")
public class RealPaymentTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired DatabaseCleanup databaseCleanup;
    @Autowired DataSeeder dataSeeder;
    @Autowired JwtProvider jwtProvider;
    @Autowired PurchaseOrderRepository orderRepository;
    @Autowired IamportClient iamportClient;

    private String userToken;
    private Member user1;
    private String impUid;
    private String merchantUid;
    private Long parentId;
    private Long categoryId;
    private Long brandId;
    private String adminToken;
    private Long cartItemId1;
    private Long cartItemId2;
    private Long cartItemId3;
    private Long cartItemId4;
    private Long orderId;
    private Long addressId1;
    private Long addressId2;
    private Long ov1;
    private Long ov2;
    private Long ov3;
    private Long ov4;
    private int payAmount;
    PurchaseOrder order1;

    @BeforeEach
    public void setUp () throws IamportResponseException, IOException {
        RestAssured.port = port;
        databaseCleanup.execute();

        //유저생성
        user1 = dataSeeder.createUser1();
        Long userId = user1.getId();

        userToken = jwtProvider.createToken(userId, Role.BUYER);

//        impUid = "imp_621687698082";
//
//        // 실제 결제건에서 merchantUid 가져오기
//        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
//        com.siot.IamportRestClient.response.Payment payment = response.getResponse();
//        merchantUid = payment.getMerchantUid(); // ✅ "order_test_1767851656861"

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

        order1 = PurchaseOrder.builder()
                .orderStatus(OrderStatus.CREATED)
                .usedPoint(0)
                .earnPoint(0)
                .payAmount(100)
                .member(user1)
                .isDeleted(false)
                .merchantUid(merchantUid)
                .build();

        order1.addOrderItem(optionVariants1, 1);
        order1.addOrderItem(optionVariants2, 2);
        order1.addOrderItem(optionVariants3, 3);
        order1.addOrderItem(optionVariants4, 4);

        PurchaseOrder purchaseOrder = orderRepository.saveAndFlush(order1);
        orderId = purchaseOrder.getId();

    }

    @DisplayName("실결제 통합 테스트")
    @Test
    public void 실결제통합테스트 (){

        //결제 전 검증
        preparePayment();

//        //결제
//        PaymentVerifyRequest request = new PaymentVerifyRequest(impUid, merchantUid, BigDecimal.valueOf(100));
//
//        RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " +userToken)
//                .body(request)
//                .when()
//                .post("api/v1/payments/verify")
//                .then().log().all()
//                .statusCode(200);
//
//        System.out.println("DB merchantUid = " + orderRepository.findAll().get(0).getMerchantUid());

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

}
