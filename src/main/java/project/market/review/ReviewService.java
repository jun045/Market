package project.market.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.OrderItem.OrderItem;
import project.market.OrderItem.OrderItemRepository;
import project.market.member.Entity.Member;
import project.market.product.Product;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

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
}
