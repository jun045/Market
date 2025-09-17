package project.market;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import project.market.Brand.Brand;
import project.market.auth.JwtProvider;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CartResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.dto.UpdateCartItemRequest;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.*;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.OptionVariantRequest;
import project.market.product.dto.ProductResponse;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class CartTest extends AcceptanceTest{

    @LocalServerPort
    int port;

    @Autowired DatabaseCleanup databaseCleanup;
    @Autowired DataSeeder dataSeeder;
    @Autowired JwtProvider jwtProvider;
    @Autowired ProductRepository productRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MemberRepository memberRepository;

    private String userToken;
    private String adminToken;
    private Long parentCategoryId;
    private Long categoryId;
    private Long brandId;
    private Long productId1;
    private Long optionVariantId1;
    private Long optionVariantId2;
    private Long optionVariantId3;
    private Long optionVariantId4;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();


        Member member1 = dataSeeder.createMember();
        Member admin1 = dataSeeder.createAdmin();

        userToken = jwtProvider.createToken(member1.getId(), Role.BUYER);
        adminToken = jwtProvider.createToken(admin1.getId(), Role.SELLER);

        Category category = dataSeeder.createCategory();
        categoryId = category.getId();
        parentCategoryId = category.getParentCategory().getId();

        Brand brand = dataSeeder.createBrand();

        categoryId = category.getId();
        brandId = brand.getId();

    }

    @DisplayName("장바구니 아이템 생성")
    @Test
    public void 장바구니아이템생성 (){

        ProductResponse product = createProduct();
        Long productId = product.id();
        Long variantId = product.variantResponseList().get(0).variantId();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateCartItemRequest(productId, variantId, 2))
                .when()
                .post("api/v1/me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);

    }

    @DisplayName("장바구니 아이템 중복 생성")
    @Test
    public void 중복생성 (){
        ProductResponse product = createProduct();
        Long productId = product.id();
        Long variantId = product.variantResponseList().get(0).variantId();

        createCartItem(productId, variantId, 2);

        CartItemResponse cartItemResponse = createCartItem(productId, variantId, 2);

        assertThat(cartItemResponse.quantity()).isEqualTo(4);

    }

    @DisplayName("장바구니 조회")
    @Test
    public void 장바구니조회(){
        ProductResponse product = createProduct();
        Long productId = product.id();
        Long variantId = product.variantResponseList().get(0).variantId();
        Long variantId2 = product.variantResponseList().get(3).variantId();

        createCartItem(productId, variantId, 2);
        createCartItem(productId, variantId2, 3);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("api/v1/me/cart")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartResponse.class);
    }

    @DisplayName("장바구니 아이템 수정")
    @Test
    public void 장바구니수정 (){
        ProductResponse product = createProduct();
        Long productId = product.id();
        Long variantId = product.variantResponseList().get(0).variantId();

        CartItemResponse cartItemResponse = createCartItem(productId, variantId, 2);

        Long cartItemId = cartItemResponse.id();

        CartItemResponse updatedResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("cartItemId", cartItemId)
                .body(new UpdateCartItemRequest(1))
                .when()
                .patch("api/v1/me/cart/items/{cartItemId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);

        assertThat(updatedResponse.quantity()).isEqualTo(1);

    }

    public ProductResponse createProduct (){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(new CreateProductRequest(
                        parentCategoryId,
                        categoryId,
                        "아이폰",
                        brandId,
                        "아이폰 16 Pro",
                        "썸네일",
                        "디테일이미지",
                        ProductStatus.SALE,
                        1600000,
                        List.of(createVariant("화이트, 128GB", 10, 0),
                                createVariant("화이트, 256GB", 20, 100000),
                                createVariant("블랙, 128gb", 15, 0),
                                createVariant("블랙, 256GB", 10, 100000))))
                .when()
                .post("seller/products/register")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

    }

    private OptionVariantRequest createVariant (String optionSummary, Integer stock, Integer extraCharge){
        return new OptionVariantRequest(optionSummary, stock, extraCharge);
    }

    private CartItemResponse createCartItem (Long productId, Long variantId, int quantity){
        return   RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(new CreateCartItemRequest(productId, variantId, quantity))
                .when()
                .post("api/v1/me/cart/items")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CartItemResponse.class);
    }



}
