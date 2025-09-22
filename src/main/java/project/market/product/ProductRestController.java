package project.market.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.member.Entity.Member;
import project.market.product.dto.*;

@RequiredArgsConstructor
@RestController
public class ProductRestController {

    private final ProductService productService;

    //상품 등록
    @PostMapping("seller/products/register")
    public ProductResponse register (@AuthenticationPrincipal (expression = "member") Member member,
                                     @RequestBody CreateProductRequest request){
        return productService.create(member, request);
    }

    //상품 수정
    @PutMapping("seller/products/{productId}")
    public ProductResponse update (@AuthenticationPrincipal (expression = "member") Member member,
                                   @RequestBody UpdateProductRequest request,
                                   @PathVariable Long productId){
        return productService.update(member, request,productId);
    }

    //상품 전체 조회
    @GetMapping("/products")
    public ProductSearchAndPagingResponse findAll(
            @RequestParam(required = false) Long categoryId,  //카테고리별 목록 보기
            @RequestParam(required = false) Long brandId,  //브랜드별 목록 보기
            @RequestParam(required = false) String keyword,  //검색결과 목록보기
            @RequestParam(defaultValue = "20") int size,  //한 페이지당 제품 개수
            @RequestParam(defaultValue = "1") int pageNumber){  //현재 페이지

       Pageable pageable = PageRequest.of(pageNumber - 1, size);
        return productService.findAll(categoryId, brandId, keyword, pageable);
    }

    //상세 조회 (가격 따로 분리?)
    @GetMapping("/products/{productId}")
    public ProductResponse findProductDetail (@PathVariable Long productId){
        return productService.findProduct(productId);
    }

    //상품 삭제
    @DeleteMapping("seller/products/{productId}")
    public void deleteProduct (@AuthenticationPrincipal (expression = "member") Member member,
                               @PathVariable Long productId){
        productService.delete(member, productId);
    }

}
