package project.market.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.Brand.Brand;
import project.market.Brand.BrandRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.dto.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ParentCategoryRepository parentCategoryRepository;

    //등록 - 관리자만 가능
    public ProductResponse create(Member member, CreateProductRequest request) {

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("상품등록은 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("상품등록은 관리자만 할 수 있습니다.");
        }

        ParentCategory parent = parentCategoryRepository.findById(request.parentCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("상위 카테고리 찾을 수 없음")
        );

        Category category = categoryRepository.findByIdAndParentCategory(request.categoryId(), parent).orElseThrow(
                () -> new RuntimeException("존재하지 않는 카테고리입니다.")
        );

        Brand brand = brandRepository.findById(request.brandId()).orElseThrow(
                () -> new RuntimeException("존재하지 않는 브랜드입니다.")
        );

        if(productRepository.findByProductName(request.name()).isPresent()){
            throw new RuntimeException("이미 존재하는 상품입니다.");
        }

        //상품 옵션
        List<OptionVariant> variants = request.variantRequest().stream().map(
                        optionVariantRequest -> OptionVariant.builder()
                                .optionSummary(optionVariantRequest.optionSummary())
                                .stock(optionVariantRequest.stock())
                                .extraCharge(optionVariantRequest.extraCharge())
                                .build()).
                toList();

        Product product = Product.builder()
                .category(category)
                .productName(request.name())
                .brand(brand)
                .description(request.description())
                .thumbnail(request.thumb())
                .detailImage(request.detailImage())
                .productStatus(request.productStatus())
                .listPrice(request.listPrice())
                .optionVariants(variants)
                .build();

        //variants -> product 접근(연관 관계 편의 메서드)
        variants.forEach(variant -> variant.setProduct(product));

        Product saved = productRepository.save(product);


        List<OptionVariantResponse> optionVariantResponses = saved.getOptionVariants().stream().map(
                response -> OptionVariantResponse.builder()
                        .variantId(response.getId())
                        .optionSummary(response.getOptionSummary())
                        .stock(response.getStock())
                        .extraCharge(response.getExtraCharge())
                        .salePrice(response.getSalePrice())
                        .build()
        ).toList();

        return new ProductResponse(
                saved.getId(),
                saved.getCategory().getParentCategory().getParentCateName(),
                saved.getCategory().getCateName(),
                saved.getBrand().getBrandName(),
                saved.getProductName(),
                saved.getDescription(),
                saved.getThumbnail(),
                saved.getDetailImage(),
                saved.getProductStatus(),
                saved.getListPrice(),
                optionVariantResponses,
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    //수정 - 관리자만 가능
    @Transactional
    public ProductResponse update(Member member, CreateProductRequest request, Long id) {

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("상품 수정은 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("상품 수정은 관리자만 할 수 있습니다.");
        }

        Product productUpdate = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 정보 찾을 수 없음"));

        productUpdate.update(
                request.name(),
                request.description(),
                request.thumb(),
                request.detailImage(),
                request.listPrice()
        );

        return new ProductResponse(
                productUpdate.getId(),
                productUpdate.getProductName(),
                productUpdate.getDescription(),
                productUpdate.getThumbnail(),
                productUpdate.getDetailImage(),
                productUpdate.getListPrice());
    }

    //전체 목록 조회 (이름, 가격만)
    public List<ProductSearchResponse> findAll(){
         List<Product> products = productRepository.findAll();
         return products.stream()
                 .map(p -> new ProductSearchResponse(
                         p.getId(),
                         p.getProductName(),
                         p.getListPrice()
                 )).toList();
    }

    //상세조회
    public ProductResponse findProduct (Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("등록된 상품이 없어 조회 불가능"));

        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getThumbnail(),
                product.getDetailImage(),
                product.getListPrice()
        );
    }


    //삭제 - 관리자만 가능
    public void delete (Member member, Long id){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("상품 삭제는 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("상품 삭제는 관리자만 할 수 있습니다.");
        }

        productRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("상품 찾을 수 없음"));

        productRepository.deleteById(id);
    }
}
