package project.market.cart;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.market.PageResponse;
import project.market.cart.dto.*;

import java.util.List;

@Component
public class CartAssembler {

    public GetCartResponse toGetCartResponse (CartRaw cartInfo, List<CartItemRaw> cartItems, CartTotalRaw cartTotalRaw, long totalElement, Pageable pageable){

        List<CartItemResponse> cartItemResponseList = CartMapper.toCartItemResponseList(cartItems);
        CartResponse cartResponse = CartMapper.toCartResponse(cartInfo, cartTotalRaw);

        PageResponse<CartItemResponse> cartItemResponsePageResponse = PageResponse.of(cartItemResponseList, totalElement, pageable);

        return CartMapper.toGetCartResponse(cartResponse, cartItemResponsePageResponse);

    }
}
