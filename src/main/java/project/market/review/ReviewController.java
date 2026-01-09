package project.market.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.member.Entity.Member;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 생성
    @PostMapping("api/v1/order_item/{orderItemId}/reviews")
    public ReviewResponse createReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long orderItemId,
                                        @Valid @RequestBody ReviewRequest request){

        return reviewService.create(member, orderItemId, request);

    }

    //리뷰 수정
    @PutMapping("api/v1/products/reviews/{reviewId}")
    public ReviewResponse updateReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long reviewId,
                                        @Valid @RequestBody ReviewRequest request){

        return reviewService.update(member, reviewId, request);
    }


}
