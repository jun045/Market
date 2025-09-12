package project.market.Brand;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.Brand.dto.BrandResponse;
import project.market.Brand.dto.CreateBrandRequest;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;

    //등록 - 관리자만 가능
    public BrandResponse create(Member member, CreateBrandRequest request) {

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("브랜드 등록은 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("브랜드 등록은 관리자만 할 수 있습니다.");
        }

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
    //수정 - 관리지만 가능
    public BrandResponse update(Member member, CreateBrandRequest request, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드 찾을 수 없음"));

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("브랜드 수정은 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("브랜드 수정은 관리자만 할 수 있습니다.");
        }

        brand.setBrandName(request.name());

        return new BrandResponse(
                brand.getId(),
                brand.getBrandName()
        );
    }

    //삭제 - 관리자만 가능
    @Transactional
    public void delete(Member member, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드 정보 일치하지 않음"));

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("브랜드 삭제는 관리자만 할 수 있습니다.")
        );

        if (!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("브랜드 삭제는 관리자만 할 수 있습니다.");
        }

        brand.deletedBrand();
    }
}
