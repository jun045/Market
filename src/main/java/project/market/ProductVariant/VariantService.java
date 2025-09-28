package project.market.ProductVariant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.ProductVariant.dto.CreateVariantRequest;
import project.market.ProductVariant.dto.AdminVariantResponse;
import project.market.ProductVariant.dto.UserVariantResponse;
import project.market.product.Product;
import project.market.product.ProductRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class VariantService {
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final OptionJsonValidator optionJsonValidator;
    private final OptionJsonParser optionJsonParser;

    //옵션 생성
    public AdminVariantResponse create(Long productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("옵션 생성 전 상품 조회 실패 : productId={}", productId);
            return new IllegalArgumentException("상품이 없습니다. productId =" + productId);
        });
        // JSON 형식 검사
        optionJsonValidator.validateLight(request.options());
        //옵션값 중복 검증
        optionJsonValidator.validateNoDuplicateValues(request.options());

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .options(request.options())
                .stock(request.stock())
                .extraCharge(request.extraCharge())
                .discountPrice(request.discountPrice())
                .isDeleted(false)
                .build();

        ProductVariant saved = variantRepository.save(variant);

        return new AdminVariantResponse(
                saved.getId(),
                saved.getOptions(),
                saved.getStock(),
                saved.getExtraCharge(),
                saved.calculateFinalPrice()
        );
    }

    //구매자용) 전체 조회 (삭제된 상품은 제외)
    @Transactional(readOnly = true)
    public List<UserVariantResponse> findAllForBuyer(Long productId) {
        if (!productRepository.existsById(productId)) {
            log.error("구매자용 옵션 조회 실패 : 상품 없음 productId={}", +productId);
            throw new IllegalArgumentException("상품이 없습니다. id=" + productId);
        }

        List<ProductVariant> variantList =
                variantRepository.findByProductIdAndIsDeletedFalse(productId);

        return variantList.stream()
                .map(v -> new UserVariantResponse(
                        v.getId(),
                        v.getOptions(),
                        v.calculateFinalPrice()
                )).toList();
    }
    //구매자용 상세조회 - 재고 보여줄거면 만들기

    //관리자용) 한 상품에 대한 전체조회 (삭제된 상품도 조회)
    @Transactional(readOnly = true)
    public List<AdminVariantResponse> findAllByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("상품이 없습니다. id=" + productId);
        }

        List<ProductVariant> variantList = variantRepository.findByProductId(productId);

        return variantList.stream()
                .map(v -> new AdminVariantResponse(
                        v.getId(),
                        v.getOptions(),
                        v.getStock(),
                        v.getExtraCharge(),
                        v.calculateFinalPrice()
                )).toList();
    }

    //상세조회
    @Transactional(readOnly = true)
    public AdminVariantResponse findOne(Long productId, Long variantId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("상세조회 실패: 상품 없음 productId={}", productId);
            return new IllegalArgumentException("상품이 없습니다. productId =" + productId);
        });

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.error("상세조회 실패 : 옵션 없음 variantId={}", variantId);
                    return new IllegalArgumentException("옵션이 없습니다. variantId =" + variantId);
                });

        if (!variant.getProduct().getId().equals(product.getId())) {
            log.error("상세조회 실패 : 해당 상품에 대한 옵션값 아님 productId={}, variantId={}", productId, variantId);
            throw new IllegalArgumentException("해당 상품에 속하지 않는 옵션입니다.");
        }

        return new AdminVariantResponse(
                variant.getId(),
                variant.getOptions(),
                variant.getStock(),
                variant.getExtraCharge(),
                variant.calculateFinalPrice()
        );
    }

    //수정
    @Transactional
    public AdminVariantResponse updateOption(Long productId, Long variantId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error(" 옵션 수정 실패 : 상품 없음 productId={}", productId);
            return new IllegalArgumentException("상품이 없습니다. productId =" + productId);
        });
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.error("옵션 수정 실패 : 옵션 없음 variantId ={}", variantId);
                    return new IllegalArgumentException("옵션이 없습니다.");
                });

        if (!variant.getProduct().getId().equals(product.getId())) {
            log.error("옵션 수정 실패 : 해당 상품에 대한 옵션값 아님 productId={}, variantId={}", productId, variantId);
            throw new IllegalArgumentException("해당 상품에 포함되는 옵션이 아닙니다.");
        }

        //json 유효성 검사
        optionJsonValidator.validateLight(request.options());
        //옵션값 중복 검증
        optionJsonValidator.validateNoDuplicateValues(request.options());

        variant.setOptions(request.options());
        variant.setStock(request.stock());
        variant.setExtraCharge(request.extraCharge());
        variant.setDiscountPrice(request.discountPrice());

        return new AdminVariantResponse(
                variant.getId(),
                variant.getOptions(),
                variant.getStock(),
                variant.getExtraCharge(),
                variant.calculateFinalPrice()
        );
    }

    //삭제
    @Transactional
    public void delete(Long productId, Long variantId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("옵션 삭제 실패 : 상품 없음 productId={}", productId);
            return new IllegalArgumentException("상품이 없습니다. productId =" + productId);
        });
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.error("옵션 삭제 실패 : 옵션 없음 variantId={}", variantId);
                    return new IllegalArgumentException("옵션이 없습니다.");
                });

        if (!variant.getProduct().getId().equals(product.getId())) {
            log.error("옵션 삭제 실패 : 해당 상품에 대한 옵션값 아님 productId={}, variantId={}", productId, variantId);
            throw new IllegalArgumentException("해당 상품에 대한 옵션이 아닙니다.");
        }
        variant.deletedOption();
    }
}
