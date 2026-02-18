package project.market.ProductVariant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.UserVariantResponse;
import project.market.member.Entity.Member;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class VariantRestController {
    private final VariantService variantService;

    //생성
    @PostMapping("/admin/products/{productId}/variants")
    public AdminVariantResponse create(@AuthenticationPrincipal (expression = "member") Member member,
                                       @PathVariable Long productId,
                                       @Valid @RequestBody CreateVariantRequest request) {
        return variantService.create(member, productId, request);
    }

    //구매자용) 전체 조회
    @GetMapping("/products/{productId}/variants")
    public List<UserVariantResponse> findAllForBuyer(@PathVariable Long productId) {
        return variantService.findAllForBuyer(productId);
    }

    //관리자용) 전체조회
    @GetMapping("/admin/products/{productId}/variants")
    public List<AdminVariantResponse> findAll(@AuthenticationPrincipal (expression = "member") Member member,
                                              @PathVariable Long productId) {
        return variantService.findAllByProductId(member, productId);
    }

    //상세 조회
    @GetMapping("/admin/products/{productId}/variants/{variantId}")
    public AdminVariantResponse findOne(@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long productId,
                                        @PathVariable Long variantId) {
        return variantService.findOne(member, productId, variantId);
    }

    //수정
    @PutMapping("/admin/products/{productId}/variants/{variantId}")
    public AdminVariantResponse updateOption(@AuthenticationPrincipal (expression = "member") Member member,
                                             @PathVariable Long productId,
                                             @PathVariable Long variantId,
                                             @Valid @RequestBody CreateVariantRequest request) {
        return variantService.updateOption(member, productId, variantId, request);
    }

    //삭제
    @DeleteMapping("/admin/products/{productId}/variants/{variantId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal (expression = "member") Member member,
                                       @PathVariable Long productId,
                                       @PathVariable Long variantId) {
        variantService.delete(member, productId, variantId);
        return ResponseEntity.noContent().build();
    }
}
