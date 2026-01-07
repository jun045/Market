package project.market;

import com.siot.IamportRestClient.IamportClient;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.market.Brand.Brand;
import project.market.Cate.Category;
import project.market.OrderItem.dto.CreateOrderItemRequest;
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.address.entity.Address;
import project.market.auth.JwtProvider;
import project.market.cart.entity.Cart;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.Product;

import java.util.List;

@ActiveProfiles("test")
public class PaymentTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired DatabaseCleanup databaseCleanup;
    @Autowired DataSeeder dataSeeder;
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

    }

    @DisplayName("결제 전 검증 테스트")
    @Test
    public void 결제전검증(){

        //주문생성
        OrderDetailResponse order = createOrder();
        orderId = order.id();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("ordersId", orderId)
                .when()
                .post("/api/v1/payments/{ordersId}/validation")
                .then().log().all()
                .statusCode(200);

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


}
