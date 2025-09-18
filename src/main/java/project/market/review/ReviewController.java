package project.market.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import project.market.member.Entity.Member;
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
                                        @RequestBody ReviewRequest request){

        return reviewService.create(member, productId, request);
    }

    //리뷰 목록 조회
    @GetMapping("api/v1/products/{productId}/review")
    public List<ReviewResponse> getAllReviews (@PathVariable Long productId){

        return reviewService.getAll(productId);
    }

    //리뷰 수정
    @PatchMapping("api/v1/products/review/{reviewId}")
    public ReviewResponse updateReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long reviewId,
                                        @RequestBody UpdateReviewRequest request){

        return reviewService.update(member, reviewId, request);
    }

    @DeleteMapping("api/v1/products/review/{reviewId}")
    public ResponseEntity<Void> deleteReview (@AuthenticationPrincipal (expression = "member") Member member,
                                              @PathVariable Long reviewId){

        reviewService.delete(member, reviewId);

        return ResponseEntity.ok().build();
    }
}
