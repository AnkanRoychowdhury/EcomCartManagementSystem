package tech.ankanroychowdhury.ecomcartmanagementsystem.adapters;

import org.springframework.stereotype.Component;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.CartItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartToCartDtoAdapterImpl implements CartToCartDtoAdapter {

    @Override
    public CartDto convertToCartDto(Cart cart) {
        return CartDto.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .cartItems(convertToCartItemDtoList(cart.getCartItems()))
                .build();
    }

    private List<CartItemDto> convertToCartItemDtoList(List<CartItem> cartItems) {
        // Map each CartItem to CartItemDto
        return cartItems.stream()
                .map(this::convertToCartItemDto)
                .collect(Collectors.toList());
    }

    private CartItemDto convertToCartItemDto(CartItem cartItem) {
        // Map CartItem to CartItemDto
        return CartItemDto.builder()
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }
}
