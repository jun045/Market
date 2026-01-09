package project.market.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.market.member.Entity.Member;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("api/v1/order_item/{orderItemId}/reviews")
    public ReviewResponse createReview (@AuthenticationPrincipal (expression = "member") Member member,
                                        @PathVariable Long orderItemId,
                                        @Valid @RequestBody ReviewRequest request){

        return reviewService.create(member, orderItemId, request);

    }


}
