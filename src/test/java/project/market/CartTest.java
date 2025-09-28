package project.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import project.market.ProductVariant.VariantRepository;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.auth.JwtProvider;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.dto.UpdateCartItemRequest;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.enums.Role;
import project.market.product.ProductRepository;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class CartTest extends AcceptanceTest{

    private static final Logger log = LoggerFactory.getLogger(CartTest.class);
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private DataSeeder dataSeeder;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String adminToken;
    private String userToken1;

    @BeforeEach
    public void setUp (){
        RestAssured.port = port;
        databaseCleanup.execute();

        Member admin = dataSeeder.createAdmin();
        adminToken = jwtProvider.createToken(admin.getId(), Role.SELLER);

        Member user1 = dataSeeder.createUser1();
        userToken1 = jwtProvider.createToken(admin.getId(), Role.BUYER);

    }

    @DisplayName("카트에 아이템 담기")
    @Test
    public void 카트아이템생성 () throws JsonProcessingException {

        //제품 생성
        ProductResponse product = createProduct(new CreateProductRequest("아이폰 16", "아이폰 16 Pro", "썸네일", "상세이미지", 1600000));
        Long productId = product.id();

        //옵션 생성
        AdminVariantResponse variant1 = createVariant(productId, new CreateVariantRequest(
                inputOptionValues(Map.of("색상", List.of("화이트"),
                "용량", List.of("256GB")))
                , 10, 0, 1500000L
        ));

        Long variantId = variant1.id();

        //장바구니 아이템 생성
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken1)
                .body(new CreateCartItemRequest(productId, variantId, 2))
                .when()
                .post("me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);
    }

    @DisplayName("장바구니 아이템 중복 추가 테스트")
    @Test
    public void 중복테스트 () throws JsonProcessingException {

        //제품 생성
        ProductResponse product = createProduct(new CreateProductRequest("아이폰 16", "아이폰 16 Pro", "썸네일", "상세이미지", 1600000));
        Long productId = product.id();

        //옵션 생성
        AdminVariantResponse variant1 = createVariant(productId, new CreateVariantRequest(
                inputOptionValues(Map.of("색상", List.of("화이트"),
                        "용량", List.of("256GB")))
                , 10, 0, 1500000L
        ));

        Long variantId = variant1.id();

        //장바구니 아이템 생성1
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken1)
                .body(new CreateCartItemRequest(productId, variantId, 2))
                .when()
                .post("me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);

        //장바구니 아이템 중복 생성
        CartItemResponse cartItemResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken1)
                .body(new CreateCartItemRequest(productId, variantId, 1))
                .when()
                .post("me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);

        assertThat(cartItemResponse.quantity()).isEqualTo(3);

    }

    @DisplayName("장바구니 아이템 수량 수정")
    @Test
    public void 수량수정 () throws JsonProcessingException {

        //제품 생성
        ProductResponse product = createProduct(new CreateProductRequest("아이폰 16", "아이폰 16 Pro", "썸네일", "상세이미지", 1600000));
        Long productId = product.id();

        //옵션 생성
        AdminVariantResponse variant1 = createVariant(productId, new CreateVariantRequest(
                inputOptionValues(Map.of("색상", List.of("화이트"),
                        "용량", List.of("256GB")))
                , 10, 0, 1500000L
        ));

        Long variantId = variant1.id();

        //장바구니 아이템 생성1
        CartItemResponse cartItemResponse = createCartItem(productId, variantId, 2);
        Long cartItemId = cartItemResponse.id();

        //장바구니 아이템 수량 수정
        CartItemResponse updatedCartItem = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken1)
                .pathParam("cartItemId", cartItemId)
                .body(new UpdateCartItemRequest(3))
                .when()
                .patch("me/cart/items/{cartItemId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);

        assertThat(updatedCartItem.quantity()).isEqualTo(3);
    }


    public ProductResponse createProduct (CreateProductRequest request){
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(request)
                .when()
                .post("/products/register")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);
    }

    public AdminVariantResponse createVariant (Long productId, CreateVariantRequest request){
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", productId)
                .body(request)
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .extract()
                .as(AdminVariantResponse.class);
    }

    public String inputOptionValues (Map<String, List<String>> options) throws JsonProcessingException {

        return objectMapper.writeValueAsString(options);

    }

    public CartItemResponse createCartItem (Long productId, Long variantId, int quantity){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken1)
                .body(new CreateCartItemRequest(productId, variantId, quantity))
                .when()
                .post("me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);
    }
}
