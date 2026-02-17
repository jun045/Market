package project.market.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.member.Entity.Member;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductRestController {

    private final ProductService productService;

    //상품 등록
    @PostMapping("/admin/products")
    public ProductResponse register(@AuthenticationPrincipal (expression = "member") Member member,
                                    @RequestBody CreateProductRequest request) {
        return productService.create(member, request);
    }

    //상품 수정
    @PutMapping("/admin/products/{productId}")
    public ProductResponse update(@AuthenticationPrincipal (expression = "member") Member member,
                                  @RequestBody CreateProductRequest request,
                                  @PathVariable Long productId) {
        return productService.update(member, request, productId);
    }

    //상품 전체 조회
    @GetMapping("/products")
    public List<ProductSearchResponse> findAll() {
        return productService.findAll();
    }

    //상세 조회
    @GetMapping("/products/{productId}")
    public ProductResponse findProductDetail(@PathVariable Long productId) {
        return productService.findProduct(productId);
    }

    //상품 삭제
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal (expression = "member") Member member,
                                              @PathVariable Long productId) {
        productService.delete(member, productId);
        return ResponseEntity.noContent().build();
    }

}
