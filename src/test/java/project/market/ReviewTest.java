package project.market;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import project.market.Brand.Brand;
import project.market.auth.JwtProvider;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.member.Entity.Member;
import project.market.member.enums.Role;
import project.market.product.Category;
import project.market.product.ProductStatus;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.OptionVariantRequest;
import project.market.product.dto.ProductResponse;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

import java.util.List;

@ActiveProfiles("test")
public class ReviewTest extends AcceptanceTest{

    @LocalServerPort
    int port;

    @Autowired DatabaseCleanup databaseCleanup;
    @Autowired DataSeeder dataSeeder;
    @Autowired JwtProvider jwtProvider;

    private String userToken;
    private String userToken2;
    private String userToken3;
    private String adminToken;
    private Long parentCategoryId;
    private Long categoryId;
    private Long brandId;


    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        //유저 생성
        Member member1 = dataSeeder.createMember();
        Member member2 = dataSeeder.createMember2();
        Member member3 = dataSeeder.createMember3();
        //관리자 생성
        Member admin1 = dataSeeder.createAdmin();


        userToken = jwtProvider.createToken(member1.getId(), Role.BUYER);
        userToken2 = jwtProvider.createToken(member2.getId(), Role.BUYER);
        userToken3 = jwtProvider.createToken(member3.getId(), Role.BUYER);
        adminToken = jwtProvider.createToken(admin1.getId(), Role.SELLER);

        //카테고리 생성
        Category category = dataSeeder.createCategory();
        categoryId = category.getId();
        parentCategoryId = category.getParentCategory().getId();

        //브랜드 생성
        Brand brand = dataSeeder.createBrand();
        brandId = brand.getId();

    }

    @DisplayName("리뷰 생성 테스트")
    @Test
    public void 리뷰생성 (){

        //제품생성
        ProductResponse productResponse = createProduct();
        Long productId = productResponse.id();
        Long variantId1 = productResponse.variantResponseList().get(0).variantId();

        //장바구니 아이템 생성
        CartItemResponse cartItemResponse = createCartItem(userToken, productId, variantId1, 3);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("productId", productId)
                .body(new ReviewRequest(5, "리뷰내용"))
                .when()
                .post("api/v1/products/{productId}/review")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReviewResponse.class);
    }

    @DisplayName("모든 리뷰 조회")
    @Test
    public void 리뷰조회(){

        //제품생성
        ProductResponse productResponse = createProduct();
        Long productId = productResponse.id();
        Long variantId1 = productResponse.variantResponseList().get(0).variantId();

        //장바구니 아이템 생성
        createCartItem(userToken, productId, variantId1, 3);
        createCartItem(userToken2, productId, variantId1, 2);
        createCartItem(userToken3, productId, variantId1, 1);

        //리뷰생성
        createReview(userToken, productId, new ReviewRequest(5, "리뷰내용1"));
        createReview(userToken2, productId, new ReviewRequest(4, "리뷰내용2"));
        createReview(userToken3, productId, new ReviewRequest(3, "리뷰내용3"));

        //리뷰조회
        List<ReviewResponse> reviewResponseList = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .pathParam("productId", productId)
                .when()
                .get("api/v1/products/{productId}/review")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", ReviewResponse.class);
    }



    //제품생성 메서드
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

    //옵션 생성 메서드
    private OptionVariantRequest createVariant (String optionSummary, Integer stock, Integer extraCharge){
        return new OptionVariantRequest(optionSummary, stock, extraCharge);
    }

    //장바구니 생성 메서드
    private CartItemResponse createCartItem (String userToken, Long productId, Long variantId, int quantity){
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

    //리뷰 생성 매서드
    private ReviewResponse createReview (String userToken, Long productId, ReviewRequest request){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("productId", productId)
                .body(request)
                .when()
                .post("api/v1/products/{productId}/review")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReviewResponse.class);
    }

}
