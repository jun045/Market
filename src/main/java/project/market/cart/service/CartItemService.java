package project.market.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.cart.CartMapper;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.dto.UpdateCartItemRequest;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.product.Product;
import project.market.product.ProductRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    //카트 아이템 생성
    public CartItemResponse create (Member member, CreateCartItemRequest request){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다.")
        );

        //최초 상품을 담을 때 장바구니 생성
        Cart cart = cartRepository.findByMemberId(user.getId()).orElseGet(
                () -> cartRepository.save(Cart.createCart(user))
        );

        Product product = productRepository.findById(request.productId()).orElseThrow(
                () -> new IllegalArgumentException("상품 정보가 잘못 되었습니다.")
        );

        //이미 장바구니에 담겨져 있는 상품과 옵션은 새로 담지 않고, 수량만 늘림
        CartItem existingItem = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), request.variantId()).orElse(null);

        if(existingItem != null){
            existingItem.increaseQuantity(request.quantity());
            return CartMapper.toCartItemResponse(existingItem);
        }

        ProductVariant productVariant = variantRepository.findById(request.variantId()).orElseThrow(
                () -> new IllegalArgumentException("옵션 정보가 잘못되었습니다.")
        );

        CartItem cartItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .productVariant(productVariant)
                .quantity(request.quantity())
                .build();

        //cartItem을 Cart에 추가하는 양방향 참조 매서드
        cart.addCart(cartItem);
        cartItemRepository.save(cartItem);

        return CartMapper.toCartItemResponse(cartItem);
    }

    @Transactional
    public CartItemResponse updateCartItem (Member member, Long cartItemId, UpdateCartItemRequest request){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다")
        );

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new IllegalArgumentException("장바구니에 등록되지 않은 상품입니다")
        );

        if(!cartItem.getCart().getMember().getId().equals(user.getId())){
            throw new IllegalArgumentException("자신의 장바구니 아이템만 수정할 수 있습니다");
        }

        cartItem.setCartItemQuantity(request.quantity());

        return CartMapper.toCartItemResponse(cartItem);
    }

    @Transactional
    public void delete (Member member, Long cartItemId){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다")
        );

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new IllegalArgumentException("장바구니에 등록되지 않은 상품입니다.")
        );

        if(!cartItem.getCart().getMember().getId().equals(user.getId())){
            throw new IllegalArgumentException("자신의 장바구니 아이템만 삭제할 수 있습니다.");
        }

        cartItemRepository.delete(cartItem);

    }
}
