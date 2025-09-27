package project.market;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import project.market.product.Product;
import project.market.product.ProductRepository;
import project.market.product.ProductStatus;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;
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

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        // 브랜드 생성 및 저장
        Brand brand = Brand.builder()
                .brandName("브랜드1")
                .build();
        brandRepository.save(brand);

        // 카테고리 생성 및 저장
        Category category = Category.builder()
                .cateName("카테고리1")
                .build();
        categoryRepository.save(category);

        Product product1 = Product.builder()
                .productName("상품이름1")
                .description("상품설명1")
                .thumbnail("썸네일1")
                .detailImage("상세이미지1")
                .listPrice(10000)
                .productStatus(ProductStatus.SALE)
                .brand(brand)
                .category(category)
                .build();

        productRepository.save(product1);
    }

    @DisplayName("상품 생성")
    @Test
    void 상품생성() {
        ProductResponse product = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/products/register") // POST /products/register 요청
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product).isNotNull();
    }

    @DisplayName("상품을 수정한다.")
    @Test
    void 상품수정() {
        //생성
        ProductResponse product = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/products/register") // POST /products/register 요청
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product).isNotNull();

        //수정
        ProductResponse 수정 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .pathParam("productId", product.id())
                .body(new CreateProductRequest(
                        "상품이름수정",
                        "상품설명수정",
                        "상품썸네일이미지수정",
                        "상품상세이미지수정",
                        11111
                ))
                .when()
                .put("/products/{productId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product.name()).isEqualTo("상품이름1");
        assertThat(수정.name()).isEqualTo("상품이름수정");
        assertThat(수정.name()).isNotEqualTo(product.name());

    }

//    @DisplayName("상품 전체를 조회한다.")
//    @Test
//    @WithMockUser
//    void 상품_전체조회() {
//        //생성1
//        ProductResponse product1 = RestAssured
//                .given().log().all()
//                .contentType(ContentType.JSON)
//                .body(new CreateProductRequest(
//                        "상품이름1",
//                        "상품설명1",
//                        "상품썸네일이미지1",
//                        "상품상세이미지1",
//                        10000
//                ))
//                .when()
//                .post("/products/register") // POST /products/register 요청
//                .then().log().all()
//                .statusCode(200)
//                .extract()
//                .as(ProductResponse.class);
//
//        assertThat(product1).isNotNull();
//
//        // 생성2 추가
//        ProductResponse product2 = RestAssured
//                .given().log().all()
//                .contentType(ContentType.JSON)
//                .body(new CreateProductRequest(
//                        "상품이름2",
//                        "상품설명2",
//                        "상품썸네일이미지2",
//                        "상품상세이미지2",
//                        10000
//                ))
//                .when()
//                .post("/products/register")
//                .then().log().all()
//                .statusCode(200)
//                .extract()
//                .as(ProductResponse.class);
//
//        assertThat(product2).isNotNull();
//
//        //전체조회
//        List<ProductSearchResponse> products = RestAssured
//                .given().log().all()
//                .when()
//                .get("/products") // 서버로 GET /products 요청
//                .then().log().all()
//                .statusCode(200)
//                .extract()
//                .jsonPath()
//                .getList(".", ProductSearchResponse.class);
//
//        assertThat(products).isNotEmpty();
//        assertThat(products)
//                .extracting(p -> p.name())
//                .contains("상품이름1", "상품이름2");
//    }

    @DisplayName("상품을 상세 조회한다")
    @Test
    void 상세조회() {
        //생성
        ProductResponse product1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/products/register") // POST /products/register 요청
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product1).isNotNull();

        //상세조회
        ProductResponse detail = RestAssured
                .given().log().all()
                .pathParam("productId", product1.id())
                .when()
                .get("/products/{productId}") // 서버로 GET /products 요청
                .then().log().all()
                .statusCode(200)
                .extract()
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
                .body(new CreateProductRequest(
                        "상품이름1",
                        "상품설명1",
                        "상품썸네일이미지1",
                        "상품상세이미지1",
                        10000
                ))
                .when()
                .post("/products/register") // POST /products/register 요청
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(product).isNotNull();

        //삭제
        RestAssured
                .given().log().all()
                .pathParam("productId", product.id())
                .when()
                .delete("/products/{productId}")
                .then().log().all()
                .statusCode(204);
    }

}
