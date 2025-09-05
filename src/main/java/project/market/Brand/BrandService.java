package project.market.Brand;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BrandService {
    private final BrandRepository brandRepository;

    //등록
    public BrandResponse create(CreateBrandRequest request) {
        Brand brand = Brand.builder()
                .brandName(request.name())
                .build();

        Brand saved = brandRepository.save(brand);

        return new BrandResponse(
                saved.getId(),
                saved.getBrandName());
    }

    //전체 조회
    public List<BrandResponse> findAll() {
        List<Brand> brands = brandRepository.findAll();

        return brands.stream()
                .map(b -> new BrandResponse(
                        b.getId(),
                        b.getBrandName()
                )).toList();
    }

    //상세 조회
    public BrandResponse findBrand(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없음"));

        return new BrandResponse(
                brand.getId(),
                brand.getBrandName());
    }

    //검색 조회 - 사용자용
    public List<BrandResponse> searchBrand(String keyword) {
        List<Brand> brands = brandRepository.findByBrandNameContainingIgnoreCase(keyword);

        return brands.stream()
                .map(brand -> new BrandResponse(
                        brand.getId(),
                        brand.getBrandName()
                )).toList();
    }

    @Transactional
    //수정
    public BrandResponse update(CreateBrandRequest request, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드 찾을 수 없음"));

        brand.setBrandName(request.name());

        return new BrandResponse(
                brand.getId(),
                brand.getBrandName()
        );
    }

    //삭제
    @Transactional
    public void delete(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드 정보 일치하지 않음"));

        brand.deletedBrand();
    }
}
