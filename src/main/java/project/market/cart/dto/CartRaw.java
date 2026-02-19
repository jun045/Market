package project.market.cart.dto;

import java.time.LocalDateTime;

public record CartRaw (Long cartId,
                       LocalDateTime updatedAt){
}
