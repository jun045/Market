package project.market;

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
import project.market.auth.JwtProvider;
import project.market.member.Entity.Member;
import project.market.member.enums.Role;
import project.market.product.Product;
import project.market.product.ProductRepository;
import project.market.product.ProductStatus;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private JwtProvider jwtProvider;

    String adminToken;

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        brandRepository.save(new Brand("브랜드1", false));
        categoryRepository.save(new Category("카테고리1", null));

        Member admin = dataSeeder.createAdmin();
        adminToken = jwtProvider.createToken(admin.getId(), Role.SELLER);
    }

    @DisplayName("상품 생성")
    @Test
    void 상품생성() {
        ProductResponse product = RestAssured
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

        assertThat(product).isNotNull();
        assertThat(product.name()).isEqualTo("상품이름1");
    }

    @DisplayName("상품을 수정한다.")
    @Test
    void 상품수정() {
        //생성
        ProductResponse product = RestAssured
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

        assertThat(product).isNotNull();

        //수정
        ProductResponse 수정 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", product.id())
                .body(new CreateProductRequest(
                        "상품이름수정",
                        "상품설명수정",
                        "상품썸네일이미지수정",
                        "상품상세이미지수정",
                        11111
                ))
                .when()
                .put("/api/v1/admin/products/{productId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product.name()).isEqualTo("상품이름1");
        assertThat(수정.name()).isEqualTo("상품이름수정");
        assertThat(수정.name()).isNotEqualTo(product.name());

    }

    @DisplayName("전체조회")
    @Test
    void 전체조회() {
        //생성1
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

        assertThat(product1).isNotNull();

        //전체조회
        Response response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        // 조회 Query, 응답시간
        logPerfMetric(response);

        List<ProductSearchResponse> products = response
                .jsonPath()
                .getList(".", ProductSearchResponse.class);

        assertThat(products).isNotEmpty();
        assertThat(products).extracting("id").contains(product1.id());
        assertThat(products).extracting("name").contains("상품이름1");
        assertThat(products).extracting("price").contains(10000);
    }

    @DisplayName("상품을 상세 조회한다")
    @Test
    void 상세조회() {
        //생성
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

        assertThat(product1).isNotNull();

        //상세조회
        Response response = RestAssured
                .given().log().all()
                .pathParam("productId", product1.id())
                .when()
                .get("/api/v1/products/{productId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        // 조회 Query, 응답시간
        logPerfMetric(response);

        ProductResponse detail = response
                .as(ProductResponse.class);

        assertThat(detail.id()).isEqualTo(product1.id());
        assertThat(detail.name()).isEqualTo("상품이름1");
    }

    @DisplayName("상품을 삭제한다")
    @Test
    void 상품삭제() {
        ProductResponse product = RestAssured
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

        assertThat(product).isNotNull();

        //삭제
        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("productId", product.id())
                .when()
                .delete("api/v1/admin/products/{productId}")
                .then().log().all()
                .statusCode(204);
    }



    private void logPerfMetric (Response res){
        PerfMetrics metrics = PerfMetrics.from(res);
        System.out.println(metrics.format());
    }

}
