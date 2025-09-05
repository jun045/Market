package project.market.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.Brand.BrandRepository;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    //등록
    public ProductResponse create(CreateProductRequest request) {
        Product product = Product.builder()
                .productName(request.name())
                .description(request.description())
                .thumbnail(request.thumb())
                .detailImage(request.detailImage())
                .listPrice(request.listPrice())
                .build();

        Product saved = productRepository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getProductName(),
                saved.getDescription(),
                saved.getThumbnail(),
                saved.getDetailImage(),
                saved.getListPrice()
        );
    }

    //수정
    @Transactional
    public ProductResponse update(CreateProductRequest request, Long id) {
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


    //삭제
    public void delete (Long id){
        productRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("상품 찾을 수 없음"));

        productRepository.deleteById(id);
    }
}
