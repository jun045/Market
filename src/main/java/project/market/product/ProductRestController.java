package project.market.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.PageResponse;
import project.market.member.Entity.Member;
import project.market.product.dto.AdminProductSearchResponse;
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
    public Page<ProductSearchResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return productService.findAll(pageable);
    }

    //관리자용 상품 조회
    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('SELLER')")
    public PageResponse<AdminProductSearchResponse> adminFindAll (@AuthenticationPrincipal (expression = "member") Member member,
                                                                  @RequestParam(required = false) Long categoryId,
                                                                  @RequestParam(required = false) Long brandId,
                                                                  @RequestParam(required = false) String brandName,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) ProductStatus productStatus,
                                                                  @RequestParam(required = false) Boolean isDeleted,
                                                                  @PageableDefault(size = 20) Pageable pageable){
        return productService.findAllForAdmin(member, categoryId, brandId, brandName, keyword, productStatus, isDeleted, pageable);

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
