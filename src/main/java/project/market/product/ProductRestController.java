package project.market.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.market.product.dto.CreateProductRequest;
import project.market.product.dto.ProductResponse;
import project.market.product.dto.ProductSearchResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProductRestController {

    private final ProductService productService;

    //상품 등록
    @PostMapping("/products/register")
    public ProductResponse register (@RequestBody CreateProductRequest request){
        return productService.create(request);
    }

    //상품 수정
    @PutMapping("/products/{productId}")
    public ProductResponse update (@RequestBody CreateProductRequest request,
                                   @PathVariable Long productId){
        return productService.update(request,productId);
    }

    //상품 전체 조회
    @GetMapping("/products")
    public List<ProductSearchResponse> findAll(){
        return productService.findAll();
    }

    //상세 조회 (가격 따로 분리?)
    @GetMapping("/products/{productId}")
    public ProductResponse findProductDetail (Long productId){
        return productService.findProduct(productId);
    }

    //상품 삭제
    @DeleteMapping("/products/{productId}")
    public void deleteProduct (@PathVariable Long productId){
        productService.delete(productId);
    }

}
