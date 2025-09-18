package project.market.review;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.product.Product;
import project.market.product.ProductRepository;
import project.market.review.dto.ReviewRequest;
import project.market.review.dto.ReviewResponse;
import project.market.review.dto.UpdateReviewRequest;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    //리뷰 생성
    public ReviewResponse create (Member member, Long productId, ReviewRequest request){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다.")
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
        );

        //TODO order 도메인 구현 후 order에서 가져와야 함
        CartItem cartItem = cartItemRepository.findByCartMemberIdAndProductId(user.getId(), product.getId()).orElseThrow(
                () -> new IllegalArgumentException("구매한 상품만 리뷰를 작성할 수 있습니다.")
        );

        Review review = Review.builder()
                .member(user)
                .product(product)
                .cartItem(cartItem)  //TODO order로 변경
                .rating(request.rating())
                .content(request.content())
                .build();

        reviewRepository.save(review);

        return ReviewMapper.toResponse(review);

    }

    //리뷰 목록 조회
    public List<ReviewResponse> getAll (Long productId){

        List<Review> reviews = reviewRepository.findAllByProductIdAndIsDeletedFalse(productId);

        return reviews.stream().map(
                        ReviewMapper::toResponse)
                .toList();

    }

    //리뷰 수정
    @Transactional
    public ReviewResponse update(Member member, Long reviewId, UpdateReviewRequest request) {

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("리뷰 수정은 로그인 후 가능합니다")
        );

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰가 존재하지 않습니다.")
        );

        if(!user.getId().equals(review.getMember().getId())){
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        //리뷰 수정
        review.update(request.rating(), review.getContent());

        return ReviewMapper.toResponse(review);

    }

    @Transactional
    public void delete (Member member, Long reviewId){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("리뷰 삭제는 로그인 후 가능합니다")
        );

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰가 존재하지 않습니다.")
        );

        if(!user.getId().equals(review.getMember().getId())){
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        review.softDelete();

    }
}
