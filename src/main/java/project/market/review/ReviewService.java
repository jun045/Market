package project.market.review;

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

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

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

        cartItem.getOptionVariant().getOptionSummary();


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
}
