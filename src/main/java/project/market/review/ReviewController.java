package project.market.review;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.market.member.Entity.Member;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("api/v1/products/{productId}/review")
    public ReviewResponse createReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long productId,
                                        @RequestBody ReviewRequest request){

        return reviewService.create(member, productId, request);
    }
}
