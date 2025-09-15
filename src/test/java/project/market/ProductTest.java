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
import project.market.Brand.BrandRepository;
import project.market.auth.JwtProvider;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;
import project.market.product.*;
import project.market.product.dto.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
public class ProductTest extends AcceptanceTest{

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ParentCategoryRepository parentCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    private String token;
    private Long categoryId;
    private Long parentId;
    private Long brandId;
    private Long memberId;

    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        Member admin = Member.builder()
                .loginId("admin")
                .password(passwordEncoder.encode("aAbB1234567890"))
                .name("관리자")
                .nickname("관리자")
                .email("admin@example.com")
                .role(Role.SELLER)
                .memberStatus(MemberStatus.ACTIVE)
                .isDeleted(false)
                .build();

        memberRepository.save(admin);

        ParentCategory parentCategory = ParentCategory.builder()
                .parentCateName("상위카테고리")
                .build();

        parentCategoryRepository.save(parentCategory);

        Category category = Category.builder()
                .parentCategory(parentCategory)
                .cateName("카테고리")
                .build();

        categoryRepository.save(category);

        Brand brand = Brand.builder()
                .brandName("애플")
                .build();

        brandRepository.save(brand);

        memberId = admin.getId();
        token = jwtProvider.createToken(memberId, admin.getRole());
        categoryId = category.getId();
        parentId = parentCategory.getId();
        brandId = brand.getId();

    }


    @DisplayName("상품생성")
    @Test
    public void 상품생성 (){
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new CreateProductRequest(
                        parentId,
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

    @DisplayName("상품수정")
    @Test
    public void 상품수정(){

        //상품등록
        ProductResponse product = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰", brandId,
                "아이폰16Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        Long productId = product.id();

        Long variantId1 = product.variantResponseList().get(0).variantId();
        Long variantId2 = product.variantResponseList().get(1).variantId();
        Long variantId3 = product.variantResponseList().get(2).variantId();
        Long variantId4 = product.variantResponseList().get(3).variantId();

        //상품수정
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UpdateProductRequest(null,
                        null,
                        "아이폰 17",
                        null,
                        "아이폰 17 pro",
                        null,
                        null,
                        null,
                        1700000,
                        List.of(updateVariant(variantId1, null, null, null),
                                updateVariant(variantId2, null, null, null),
                                updateVariant(variantId3, null, null, null),
                                updateVariant(variantId4, "블랙, 512GB", 20, 200000))))
                .when()
                .pathParam("productId", productId)
                .put("seller/products/{productId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

    }

    @DisplayName("상품목록조회")
    @Test
    public void 상품목록조회(){

        ProductResponse product1 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰 16 Pro", brandId,
                "아이폰16Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        ProductResponse product2 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰 17 Pro", brandId,
                "아이폰17 Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        ProductResponse product3 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰 17", brandId,
                "아이폰17",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1400000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        ProductResponse product4 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이패드 11", brandId,
                "아이패드 11 air wifi",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1000000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", ProductSearchResponse.class);

    }

    @DisplayName("상품상세조회")
    @Test
    public void 상품상세조회(){
        //상품등록
        ProductResponse product = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰", brandId,
                "아이폰16Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        Long productId = product.id();

        //상세조회
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("productId", productId)
                .when()
                .get("/products/{productId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

    }

    @DisplayName("상품삭제")
    @Test
    public void 상품삭제 (){
        //상품 생성1
        ProductResponse product1 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰 16 Pro", brandId,
                "아이폰16Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        Long productId1 = product1.id();

        //상품 생성2
        ProductResponse product2 = createProduct(new CreateProductRequest(parentId,
                categoryId,
                "아이폰 17 Pro", brandId,
                "아이폰17 Pro",
                "썸네일",
                "디테일이미지",
                ProductStatus.SALE,
                1600000,
                List.of(createVariant("화이트, 128GB", 10, 0),
                        createVariant("화이트, 256GB", 20, 100000),
                        createVariant("블랙, 128gb", 15, 0),
                        createVariant("블랙, 256GB", 10, 100000))));

        //상품1 삭제
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("productId", productId1)
                .when()
                .delete("seller/products/{productId}")
                .then().log().all()
                .statusCode(200);

        List<ProductSearchResponse> responseList = getList();

        assertThat(responseList.size()).isEqualTo(1);

    }


    private OptionVariantRequest createVariant (String optionSummary, Integer stock, Integer extraCharge){
        return new OptionVariantRequest(optionSummary, stock, extraCharge);
    }

    private UpdateOptionVariantRequest updateVariant (Long variantId, String optionSummary, Integer stock, Integer extraCharge){
        return new UpdateOptionVariantRequest(variantId, optionSummary, stock, extraCharge);
    }

    private ProductResponse createProduct (CreateProductRequest productRequest){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .when()
                .post("seller/products/register")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);
    }

    private List<ProductSearchResponse> getList (){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/products")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", ProductSearchResponse.class);
    }


}
