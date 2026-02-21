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
                .getList("content", ProductSearchResponse.class);

        int totalElements = response.jsonPath().getInt("totalElements");
        int totalPages = response.jsonPath().getInt("totalPages");
        int size = response.jsonPath().getInt("size");

        assertThat(products).isNotEmpty();
        assertThat(products).extracting("id").contains(product1.id());
        assertThat(products).extracting("productName").contains("상품이름1");
        assertThat(products).extracting("price").contains(10000);
        assertThat(totalElements).isGreaterThan(0);
        assertThat(size).isEqualTo(20);  // 기본값 20
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

    @DisplayName("전체조회 페이징")
    @Test
    void 전체조회_페이징() {
        //given : 상품 25개 생성
        for (int i = 0; i < 25; i++) {
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .body(new CreateProductRequest(
                            "상품"+ i,
                            "상품설명" +i,
                            "상품썸네일" +i,
                            "상품상세"+i,
                            10000+i
                    ))
                    .when()
                    .post("/api/v1/admin/products")
                    .then().log().all()
                    .statusCode(200);
        }
        //when :  첫페이지 조회 ( 기본값 size = 20)
        Response response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .queryParam("page",0)
                .queryParam("size", 20)
                .when()
                .get("/api/v1/products")
                .then().log().all()
                .extract()
                .response();

        //then
        List<ProductSearchResponse> products = response
                .jsonPath()
                .getList("content", ProductSearchResponse.class);

        int totalElements = response.jsonPath().getInt("totalElements");
        int totalPages = response.jsonPath().getInt("totalPages");
        int size = response.jsonPath().getInt("size");
        int numberOfElements = response.jsonPath().getInt("numberOfElements");

        assertThat(products).hasSize(20);  // 첫 페이지는 20개
        assertThat(totalElements).isGreaterThanOrEqualTo(25);  // 전체 25개 이상
        assertThat(totalPages).isGreaterThanOrEqualTo(2);  // 최소 2페이지
        assertThat(size).isEqualTo(20);  // 페이지 크기 20
        assertThat(numberOfElements).isEqualTo(20);  // 실제 반환된 개수 20

        assertThat(products.get(0).id()).isNotNull();
        assertThat(products.get(0).productName()).isNotNull();
        assertThat(products.get(0).price()).isGreaterThan(0);
    }

    private void logPerfMetric (Response res){
        PerfMetrics metrics = PerfMetrics.from(res);
        System.out.println(metrics.format());
    }

}
