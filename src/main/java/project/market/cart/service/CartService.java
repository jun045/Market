package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.repository.CartRepository;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

}
