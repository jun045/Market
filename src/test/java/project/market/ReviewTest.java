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
import project.market.ProductVariant.ProductVariant;
import project.market.PurchaseOrder.PurchaseOrderRepository;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.address.entity.Address;
import project.market.auth.JwtProvider;
import project.market.cart.entity.Cart;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.Product;
import project.market.review.dto.DeleteReviewResponse;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class ReviewTest extends AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    DataSeeder dataSeeder;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PurchaseOrderRepository orderRepository;

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
    private Long oi1;
    private Long oi2;
    private Long oi3;
    private Long oi4;


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

        PurchaseOrder order =dataSeeder.createCompletedOrder();

        oi1 = order.getOrderItems().get(0).getId();
        oi2 = order.getOrderItems().get(1).getId();
        oi3 = order.getOrderItems().get(2).getId();
        oi4 = order.getOrderItems().get(3).getId();
    }

    @DisplayName("리뷰 생성 테스트")
    @Test
    public void 리뷰생성 (){
        ReviewResponse reviewResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("orderItemId", oi1)
                .body(new ReviewRequest(5, "리뷰내용"))
                .when()
                .post("api/v1/order_item/{orderItemId}/reviews")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReviewResponse.class);

    }

    @DisplayName("리뷰 수정 테스트")
    @Test
    public void 리뷰수정 (){

        //리뷰 생성
        ReviewResponse reviewResponse = createReview();
        Long reviewId = reviewResponse.reviewId();

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("reviewId", reviewId)
                .body(new ReviewRequest(4, "수정된 내용"))
                .when()
                .put("api/v1/products/reviews/{reviewId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReviewResponse.class);
    }

    @DisplayName("리뷰 삭제 테스트")
    @Test
    public void 리뷰삭제 (){

        ReviewResponse review = createReview();
        Long reviewId = review.reviewId();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("reviewId", reviewId)
                .when()
                .delete("api/v1/products/reviews/{reviewId}/delete")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(DeleteReviewResponse.class);
    }

    @DisplayName("관리자 리뷰 삭제 테스트")
    @Test
    public void 관리자리뷰삭제 (){

        ReviewResponse review = createReview();
        Long reviewId = review.reviewId();

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("reviewId", reviewId)
                .when()
                .delete("api/v1/admin/products/reviews/{reviewId}/delete")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(DeleteReviewResponse.class);
    }





    private ReviewResponse createReview (){
         return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .pathParam("orderItemId", oi1)
                .body(new ReviewRequest(5, "리뷰내용"))
                .when()
                .post("api/v1/order_item/{orderItemId}/reviews")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReviewResponse.class);
    }




}
