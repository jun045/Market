package project.market.ProductVariant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.UserVariantResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class VariantRestController {
    private final VariantService variantService;

    //생성
    @PostMapping("/admin/products/{productId}/variants")
    public AdminVariantResponse create(@PathVariable Long productId,
                                       @Valid @RequestBody CreateVariantRequest request) {
        return variantService.create(productId, request);
    }

    //구매자용) 전체 조회
    @GetMapping("/products/{productId}/variants")
    public List<UserVariantResponse> findAllForBuyer(@PathVariable Long productId) {
        return variantService.findAllForBuyer(productId);
    }

    //관리자용) 전체조회
    @GetMapping("/admin/products/{productId}/variants")
    public List<AdminVariantResponse> findAll(@PathVariable Long productId) {
        return variantService.findAllByProductId(productId);
    }

    //상세 조회
    @GetMapping("/admin/products/{productId}/variants/{variantId}")
    public AdminVariantResponse findOne(@PathVariable Long productId,
                                        @PathVariable Long variantId) {
        return variantService.findOne(productId, variantId);
    }

    //수정
    @PutMapping("/admin/products/{productId}/variants/{variantId}")
    public AdminVariantResponse updateOption(@PathVariable Long productId,
                                             @PathVariable Long variantId,
                                             @Valid @RequestBody CreateVariantRequest request) {
        return variantService.updateOption(productId, variantId, request);
    }

    //삭제
    @DeleteMapping("/admin/products/{productId}/variants/{variantId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId,
                                       @PathVariable Long variantId) {
        variantService.delete(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}
