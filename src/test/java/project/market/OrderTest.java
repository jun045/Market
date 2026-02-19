package project.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import project.market.Brand.Brand;
import project.market.Brand.BrandRepository;
import project.market.Cate.Category;
import project.market.Cate.CategoryRepository;
import project.market.OrderItem.dto.CreateOrderItemRequest;
import project.market.OrderItem.dto.OrderItemDetailResponse;
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.PurchaseOrder.dto.CreateCartOrderRequest;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.PurchaseOrder.dto.OrderListResponse;
import project.market.auth.JwtProvider;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.ProductRepository;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String userToken;
    private Long testVariantId;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestAssured.port = port;
        databaseCleanup.execute();

        brandRepository.save(new Brand("브랜드1", false));
        categoryRepository.save(new Category("카테고리1", null));

        //관리자 생성
        Member admin = dataSeeder.createAdmin();
        adminToken = jwtProvider.createToken(admin.getId(), Role.SELLER);

        //일반 유저 생성
        Member user1 = dataSeeder.createUser1();
        userToken = jwtProvider.createToken(user1.getId(), Role.BUYER);

        //상품 생성
        ProductResponse product = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/api/v1/admin/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        //옵션 json생성 - 화이트,M
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("화이트"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        //옵션 생성
        AdminVariantResponse variant = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", product.id())
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        10,
                        0,
                        null
                ))
                .when()
                .post("/api/v1/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        testVariantId = variant.id();
    }

    @DisplayName("단일 주문 생성")
    @Test
    void 주문생성() throws JsonProcessingException {
        CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest(testVariantId, 2);

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateOrderRequest(0,
                        List.of(orderItemRequest)
                ))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        assertThat(order).isNotNull();
        assertThat(order.orderTotalPrice()).isEqualTo(20000);
        assertThat(order.orderItems()).hasSize(1); //상품 항목 1개인지 확인
        assertThat(order.orderItems().get(0).quantity()).isEqualTo(2); //첫번째 주문 항목의 수량
    }

    @DisplayName("여러 상품 한번에 주문 생성")
    @Test
    void 주문생성_여러상품() throws JsonProcessingException {
        //1번 상품 생성
        ProductResponse product1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/api/v1/admin/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        //2번 상품 생성
        ProductResponse product2 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(new CreateProductRequest(
                        "상품이름2",
                        "상품설명2",
                        "상품썸네일이미지2",
                        "상품상세이미지2",
                        20000
                ))
                .when()
                .post("/api/v1/admin/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        //1번 상품 옵션 생성
        Map<String, List<String>> options1 = new HashMap<>();
        options1.put("색상", List.of("블랙"));
        String options1Json = objectMapper.writeValueAsString(options1);

        AdminVariantResponse variant1 = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", product1.id())
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        options1Json,
                        100,
                        0,
                        null))
                .when()
                .post("/api/v1/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        //2번 상품 옵션 생성
        Map<String, List<String>> options2 = new HashMap<>();
        options2.put("색상", List.of("화이트"));
        String options2Json = objectMapper.writeValueAsString(options2);

        AdminVariantResponse variant2 = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", product2.id())
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        options2Json,
                        100,
                        1000,
                        null))
                .when()
                .post("/api/v1/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        //여러 상품 주문
        List<CreateOrderItemRequest> orderItems = List.of(
                new CreateOrderItemRequest(variant1.id(), 2),
                new CreateOrderItemRequest(variant2.id(), 1)
        );

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(new CreateOrderRequest(0,
                        orderItems))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        assertThat(order).isNotNull();
        assertThat(order.orderItems()).hasSize(2);
        assertThat(order.orderTotalPrice()).isEqualTo(41000); //(10000*2)+(20000+옵션1000)
    }

    //근데 사용자한테 토큰? 확인하는게 맞는지?
    @DisplayName("사용자 주문 전체 조회")
    @Test
    void 사용자_주문전체조회() {
        //주문 생성
        CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest(testVariantId, 1);

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateOrderRequest(0,
                        List.of(orderItemRequest)
                ))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        //조회
        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        logPerfMetric(response);

        List<OrderListResponse> orders = response
                .jsonPath()
                .getList(".", OrderListResponse.class);

        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).orderTotalPrice()).isEqualTo(10000);
    }

    @DisplayName("관리자용 주문 전체 조회")
    @Test
    void 관리자_주문전체조회() throws JsonProcessingException {
        //주문 생성
        CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest(testVariantId, 1);

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateOrderRequest(0,
                        List.of(orderItemRequest)
                ))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        //조회
        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/v1/admin/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        logPerfMetric(response);

        List<OrderListResponse> orders = response
                .jsonPath()
                .getList(".", OrderListResponse.class);

        assertThat(orders).isNotEmpty();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).id()).isEqualTo(order.id()); //조회 orders와 주문order 일치하는지 확인
        assertThat(orders.get(0).orderTotalPrice()).isEqualTo(10000);
    }

    @DisplayName("사용자용 주문 상세 조회")
    @Test
    void 사용자_주문상세조회() throws JsonProcessingException {
        //주문 생성
        CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest(testVariantId, 1);

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateOrderRequest(0,
                        List.of(orderItemRequest)
                ))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        Long orderId = order.id();

        //주문 상세 조회
        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + userToken)
                .pathParam("orderId", orderId)
                .when()
                .get("/api/v1/orders/{orderId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        logPerfMetric(response);

        OrderDetailResponse orderDetail = response
                .as(OrderDetailResponse.class);

        assertThat(orderDetail).isNotNull();
        assertThat(orderDetail.id()).isEqualTo(orderId);
        assertThat(orderDetail.orderItems()).hasSize(1);
        assertThat(orderDetail.orderItems().get(0).quantity()).isEqualTo(1);
    }

    @DisplayName("관리자용 주문 상세 조회")
    @Test
    void 관리자_주문상세조회() throws JsonProcessingException {
        //주문 생성
        CreateOrderItemRequest orderItemRequest = new CreateOrderItemRequest(testVariantId, 1);

        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken) //관리자로 주문하려고 하면 error 409
                .body(new CreateOrderRequest(0,
                        List.of(orderItemRequest)
                ))
                .when()
                .post("/api/v1/orders")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        Long orderId = order.id();

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("orderId", orderId)
                .when()
                .get("/api/v1/admin/orders/{orderId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        // 조회 Query, 응답시간
        logPerfMetric(response);

        OrderDetailResponse orderDetail = response
                .as(OrderDetailResponse.class);

        assertThat(orderDetail).isNotNull();
        assertThat(orderDetail.id()).isEqualTo(orderId);
    }

    @DisplayName("장바구니 상품 주문 생성")
    @Test
    void 장바구니상품_주문생성() {
        Member user = memberRepository.findByLoginId("userId1").orElseThrow();

        //장바구니 생성
        Cart cart = Cart.builder()
                .member(user)
                .build();

        cartRepository.save(cart);

        //옵션 다시 조회(영속성 확보)
        ProductVariant variant1 = variantRepository.findById(testVariantId)
                .orElseThrow(() -> new IllegalArgumentException("옵션 없음"));

        //cartItem 2개 생성, 저장
        CartItem item1 = CartItem.builder()
                .cart(cart)
                .product(variant1.getProduct())
                .productVariant(variant1)
                .quantity(1)
                .build();
        cart.addCart(item1);
        cartItemRepository.save(item1);

        CartItem item2 = CartItem.builder()
                .cart(cart)
                .product(variant1.getProduct())
                .productVariant(variant1)
                .quantity(2)
                .build();
        cart.addCart(item2);
        cartItemRepository.save(item2);

        List<Long> cartItemIds = List.of(item1.getId(), item2.getId());

        //장바구니 주문
        OrderDetailResponse order = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateCartOrderRequest(cartItemIds))
                .when()
                .post("/api/v1/orders/cart")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(OrderDetailResponse.class);

        assertThat(order).isNotNull();
        assertThat(order.orderItems()).hasSize(1);
        //옵션 같을때 합침
        OrderItemDetailResponse item = order.orderItems().get(0);
        assertThat(item.quantity()).isEqualTo(3);
        assertThat(item.totalPrice()).isEqualTo(30000);

        //장바구니에 남은 상품 검증
        List<CartItem> remaining = cartItemRepository.findAllByCartId(cart.getId());
        assertThat(remaining).hasSize(0);
    }



    private void logPerfMetric (Response res){
        PerfMetrics metrics = PerfMetrics.from(res);
        System.out.println(metrics.format());
    }
}
