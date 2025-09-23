package project.market.ProductVariantPractice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.ProductVariantPractice.dto.CreateVariantRequest;
import project.market.ProductVariantPractice.dto.VariantResponse;
import project.market.product.Product;
import project.market.product.ProductRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VariantService {
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    //옵션 생성
    public VariantResponse create(Long productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .options(request.options())
                .stock(request.stock())
                .build();

        ProductVariant saved = variantRepository.save(variant);

        return new VariantResponse(
                saved.getId(),
                saved.getOptions(),
                saved.getStock()
        );
    }

    //한 상품에 대한 전체조회
    public List<VariantResponse> findAllByProductId(Long productId) {
        List<ProductVariant> variantList = variantRepository.findByProductId(productId);

        return variantList.stream()
                .map(v -> new VariantResponse(
                        v.getId(),
                        v.getOptions(),
                        v.getStock()
                )).toList();
    }

    //상세조회
    public VariantResponse findOne(Long productId, Long variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다." + productId));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("옵션이 없습니다." + variantId));

        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("해당 상품(" + productId + ")에 속하지 않는 옵션(" + variantId + ")입니다.");
        }

        return new VariantResponse(
                variant.getId(),
                variant.getOptions(),
                variant.getStock()
        );
    }

    //수정
    @Transactional
    public VariantResponse updateOption(Long productId, Long variantId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));
        ProductVariant variant = variantRepository.findById(variantId).orElseThrow(() -> new IllegalArgumentException("옵션이 없습니다."));

        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("해당 상품에 포함되는 옵션이 아닙니다.");
        }

        variant.setOptions(request.options());
        variant.setStock(request.stock());

        variantRepository.save(variant);

        return new VariantResponse(
                variant.getId(),
                variant.getOptions(),
                variant.getStock()
        );
    }

    //삭제
    @Transactional
    public void delete(Long productId, Long optionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));
        ProductVariant variant = variantRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션이 없습니다."));

        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("해당 상품에 대한 옵션이 아닙니다.");
        }
        variant.deletedOption();
        variantRepository.save(variant);
    }
}
