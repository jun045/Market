package project.market.Brand;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.Brand.dto.BrandResponse;
import project.market.Brand.dto.CreateBrandRequest;
import project.market.member.Entity.Member;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BrandController {

    private final BrandService brandService;

    //등록
    @PostMapping("/brands/register")
    public BrandResponse create(@AuthenticationPrincipal (expression = "member") Member member,
                                @RequestBody CreateBrandRequest request) {
        return brandService.create(member, request);
    }

    //전체 조회
    @GetMapping("/brands")
    public List<BrandResponse> findAll() {
        return brandService.findAll();
    }

    //상세 조회
    @GetMapping("/brands/{brandId}")
    public BrandResponse findOneBrand(@PathVariable Long brandId) {
        return brandService.findBrand(brandId);
    }

    //검색
    @GetMapping("/brands/search")
    public List<BrandResponse> searchBrands(@RequestParam String name) {
        return brandService.searchBrand(name);
    }

    //수정
    @PutMapping("/brands/{brandId}")
    public BrandResponse update(@AuthenticationPrincipal (expression = "member") Member member,
                                @RequestBody CreateBrandRequest request,
                                @PathVariable Long brandId) {
        return brandService.update(member, request,brandId);
    }

    //삭제
    @DeleteMapping("/brands/{brandId}")
    public void delete (@AuthenticationPrincipal (expression = "member") Member member,
                        @PathVariable Long brandId){
        brandService.delete(member, brandId);
    }

}
