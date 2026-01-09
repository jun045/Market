package project.market.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.PageResponse;
import project.market.member.Entity.Member;
import project.market.review.dto.DeleteReviewResponse;
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

    @DeleteMapping("api/v1/products/reviews/{reviewId}/delete")
    public DeleteReviewResponse deleteReview (@AuthenticationPrincipal (expression = "member") Member member,
                                              @PathVariable Long reviewId){
        return reviewService.delete(member, reviewId);
    }

    @DeleteMapping("api/v1/admin/products/reviews/{reviewId}/delete")
    @PreAuthorize("hasRole('SELLER')")
    public DeleteReviewResponse deleteForAdmin (@AuthenticationPrincipal (expression = "member") Member member,
                                                @PathVariable Long reviewId){

        return reviewService.adminDelete(member, reviewId);
    }

    @GetMapping("api/v1/products/{productId}/reviews")
    public PageResponse<ReviewResponse> getAllReviews (@PathVariable Long productId,
                                                       @RequestParam (defaultValue = "1") int pageNumber,
                                                       @RequestParam (defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(pageNumber -1, size);
        return reviewService.getAll(productId, pageable);
    }


}
