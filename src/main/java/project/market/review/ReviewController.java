package project.market.review;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.member.Entity.Member;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

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
}
