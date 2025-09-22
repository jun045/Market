package project.market.ProductVariantPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.market.ProductVariantPractice.dto.CreateVariantRequest;
import project.market.ProductVariantPractice.dto.VariantResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class VariantRestController {
    private final VariantService variantService;

    //생성
    @PostMapping("/products/{productId}/variants")
    public VariantResponse create(@PathVariable Long productId,
                                  @RequestBody CreateVariantRequest request) {
        return variantService.create(productId, request);
    }

    //전체조회
    @GetMapping("/products/{productId}/variants")
    public List<VariantResponse> findAll(@PathVariable Long productId) {
        return variantService.findAllByProductId(productId);
    }

    //상세 조회
    @GetMapping("/products/{productId}/variants/{variantId}")
    public VariantResponse findOne(@PathVariable Long productId,
                                   @PathVariable Long variantId) {
        return variantService.findOne(productId, variantId);
    }

    //수정
    @PutMapping("/products/{productId}/variants/{variantId}")
    public VariantResponse update(@PathVariable Long productId,
                                  @PathVariable Long variantId,
                                  @RequestBody CreateVariantRequest request) {
        return variantService.updateOption(productId, variantId, request);
    }

    //삭제
    @DeleteMapping("/products/{productId}/variants/{variantId}")
    public void delete(@PathVariable Long productId,
                       @PathVariable Long variantId) {
        variantService.delete(productId, variantId);
    }
}
