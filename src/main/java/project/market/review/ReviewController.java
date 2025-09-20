package project.market.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import project.market.member.Entity.Member;
import project.market.review.dto.ReviewAndPagingResponse;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;
import project.market.review.dto.UpdateReviewRequest;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 생성
    @PostMapping("api/v1/products/{productId}/review")
    public ReviewResponse createReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long productId,
                                        @Valid @RequestBody ReviewRequest request){

        return reviewService.create(member, productId, request);
    }

    //리뷰 목록 조회
    @GetMapping("api/v1/products/{productId}/review")
    public ReviewAndPagingResponse getAllReviews (@PathVariable Long productId,
                                                  @RequestParam (defaultValue = "1")int pageNumber,
                                                  @RequestParam (defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(pageNumber -1, size);
        return reviewService.getAll(productId, pageable);
    }

    //리뷰 수정
    @PatchMapping("api/v1/products/reviews/{reviewId}")
    public ReviewResponse updateReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long reviewId,
                                        @Valid @RequestBody UpdateReviewRequest request){

        return reviewService.update(member, reviewId, request);
    }

    //리뷰 삭제
    @DeleteMapping("api/v1/products/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview (@AuthenticationPrincipal (expression = "member") Member member,
                                              @PathVariable Long reviewId){

        reviewService.delete(member, reviewId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("api/v1/seller/products/reviews/{reviewId}")
    public ResponseEntity<Void> deleteForSeller (@AuthenticationPrincipal (expression = "member") Member member,
                                                 @PathVariable Long reviewId){

        reviewService.sellerDelete(member, reviewId);

        return ResponseEntity.ok().build();
    }
}
