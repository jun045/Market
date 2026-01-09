package project.market.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.OrderItem.OrderItem;
import project.market.OrderItem.OrderItemRepository;
import project.market.member.Entity.Member;
import project.market.product.Product;
import project.market.review.dto.DeleteReviewResponse;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    //리뷰 생성
    public ReviewResponse create (Member member, Long orderItemId, ReviewRequest request){

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new IllegalArgumentException("해당 상품의 주문을 찾을 수 없습니다.")
        );

        //리뷰 작성 가능 상태 검증
        orderItem.validateReviewable(member);

        Product product = orderItem.getProductVariant().getProduct();

        //리뷰 생성
        Review review = Review.createReview(member, product, orderItem, request.rating(), request.content());

        //리뷰 작성 완료 표기
        orderItem.markReviewed();
        reviewRepository.save(review);

        return ReviewResponse.from(review);

    }

    //리뷰 수정
    @Transactional
    public ReviewResponse update (Member member, Long reviewId, ReviewRequest request){

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰를 찾을 수 없습니다.")
        );

        //리뷰 작성자 검증
        review.validateReviewOwner(member);

        //리뷰 수정
        review.updateReview(request.rating(), request.content());

        return ReviewResponse.from(review);
    }

    @Transactional
    public DeleteReviewResponse delete (Member member, Long reviewId){

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰를 찾을 수 없습니다.")
        );

        //리뷰 작성자 검증
        review.validateReviewOwner(member);

        //리뷰 삭제
        review.softDelete();
        review.getOrderItem().unMarkReviewed();

        return DeleteReviewResponse.from(review);
    }
}
