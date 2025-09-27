package project.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.ProductVariant.dto.UserVariantResponse;
import project.market.product.Product;
import project.market.product.ProductRepository;
import project.market.product.ProductStatus;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OptionTest {

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
    private VariantRepository variantRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        brandRepository.save(new Brand("브랜드1", false));
        categoryRepository.save(new Category("카테고리1", null));
    }

    @DisplayName("옵션 생성")
    @Test
    void 옵션생성() throws JsonProcessingException{
        //1. 상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        //2. 옵션 JSON 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("화이트"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        //3. 옵션 생성 POST
        AdminVariantResponse variant = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        10,
                        0,
                        null
                ))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        assertThat(variant).isNotNull();
        assertThat(variant.options()).contains("화이트");
    }

    @DisplayName("구매자용 전체조회")
    @Test
    void 구매자_전체조회() throws JsonProcessingException{
        //상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        // 2. 옵션 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("블랙"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(optionsJson, 10, 0, null))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200);

        // 3. 구매자용 전체조회
        List<UserVariantResponse> variants = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .when()
                .get("/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", UserVariantResponse.class);

        assertThat(variants).isNotEmpty();
        assertThat(variants.get(0).options()).contains("블랙");
    }

    @DisplayName("관리자용 전체조회")
    @Test
    void 관리자_전체조회() throws JsonProcessingException {
        //상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        //옵션 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("레드"));
        optionsMap.put("사이즈", List.of("L"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        20,
                        1000,
                        null))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200);

        // 3. 관리자용 전체조회
        List<AdminVariantResponse> variants = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .when()
                .get("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", AdminVariantResponse.class);

        assertThat(variants).isNotEmpty();
        assertThat(variants.get(0).options()).contains("레드");
    }

    @DisplayName("옵션 상세 조회")
    @Test
    void 옵션상세조회() throws JsonProcessingException {
        //상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        //옵션 JSON 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("블랙"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        AdminVariantResponse variant = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        10,
                        0,
                        null
                ))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        Long variantId = variant.id();

        //상세조회
        AdminVariantResponse detail = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .pathParam("variantId", variantId)
                .when()
                .get("/admin/products/{productId}/variants/{variantId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        assertThat(detail).isNotNull();
        assertThat(detail.options()).contains("블랙");
    }

    @DisplayName("옵션 수정")
    @Test
    void 옵션수정() throws JsonProcessingException {
        //상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        //옵션 JSON 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("블랙"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        AdminVariantResponse variant = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        10,
                        0,
                        null
                ))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        Long variantId = variant.id();

        // 수정할 옵션 JSON 생성
        Map<String, List<String>> updatedOptionsMap = new HashMap<>();
        updatedOptionsMap.put("색상", List.of("블루")); // 변경할 색상
        updatedOptionsMap.put("사이즈", List.of("L"));  // 변경할 사이즈
        String updatedOptionsJson = objectMapper.writeValueAsString(updatedOptionsMap);

        AdminVariantResponse update = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .pathParam("variantId", variantId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        updatedOptionsJson,
                        50,
                        5000,
                        null))
                .when()
                .put("/admin/products/{productId}/variants/{variantId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        assertThat(update).isNotNull();
        assertThat(update.options()).contains("블루");
        assertThat(update.stock()).isEqualTo(50);
        assertThat(update.extraCharge()).isEqualTo(5000);
    }

    @DisplayName("옵션 삭제")
    @Test
    void 옵션삭제() throws JsonProcessingException{
        //상품 생성
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
                .post("/products/register") //POST
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        Long productId = product.id();

        //옵션 JSON 생성
        Map<String, List<String>> optionsMap = new HashMap<>();
        optionsMap.put("색상", List.of("블랙"));
        optionsMap.put("사이즈", List.of("M"));
        String optionsJson = objectMapper.writeValueAsString(optionsMap);

        AdminVariantResponse variant = RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .contentType(ContentType.JSON)
                .body(new CreateVariantRequest(
                        optionsJson,
                        10,
                        0,
                        null
                ))
                .when()
                .post("/admin/products/{productId}/variants")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AdminVariantResponse.class);

        Long variantId = variant.id();

        //삭제
        RestAssured
                .given().log().all()
                .pathParam("productId", productId)
                .pathParam("variantId", variantId)
                .contentType(ContentType.JSON)
                .when()
                .delete("/admin/products/{productId}/variants/{variantId}")
                .then().log().all()
                .statusCode(204);
    }
}
