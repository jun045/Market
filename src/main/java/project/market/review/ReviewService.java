package project.market.review;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.market.PageInfo;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;
import project.market.product.Product;
import project.market.product.ProductRepository;
import project.market.review.dto.ReviewAndPagingResponse;
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
    private final ReviewQueryRepository reviewQueryRepository;

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
    public ReviewAndPagingResponse getAll (Long productId, Pageable pageable){

//        List<Review> reviews = reviewRepository.findAllByProductIdAndIsDeletedFalse(productId);

        int pageNumber = pageable.getPageNumber();
        int size = pageable.getPageSize();

        List<Review> reviews = reviewQueryRepository.getReviewsAndPaging(productId, pageNumber, size);

        List<ReviewResponse> reviewResponses = reviews.stream().map(
                        ReviewMapper::toResponse)
                .toList();

        long totalReviews = reviewQueryRepository.getTotalReviews(productId);
        int totalPage = (int) Math.ceil((double) totalReviews/size);

        PageInfo pageInfo = new PageInfo(pageNumber + 1, size, totalReviews, totalPage);

        return ReviewMapper.toReviewPagingResponse(reviewResponses, pageInfo);
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

    //리뷰 삭제
    @Transactional
    public void delete (Member member, Long reviewId){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("리뷰 삭제는 로그인 후 가능합니다.")
        );

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰가 존재하지 않습니다.")
        );

        if(!user.getId().equals(review.getMember().getId())){
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        review.softDelete();

    }

    @Transactional
    public void sellerDelete(Member member, Long reviewId) {

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("리뷰 삭제는 로그인 후 가능합니다.")
        );

        if(!user.getRole().equals(Role.SELLER)){
            throw new IllegalArgumentException("관리자 모드입니다. 관리자만 삭제 가능합니다.");
        }

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("리뷰가 존재하지 않습니다.")
        );

        review.softDelete();
    }
}
