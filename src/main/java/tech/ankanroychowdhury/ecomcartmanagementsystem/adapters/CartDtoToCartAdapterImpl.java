package tech.ankanroychowdhury.ecomcartmanagementsystem.adapters;

import org.springframework.stereotype.Component;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.CartItem;

import java.util.ArrayList;
import java.util.Date;

@Component
public class CartDtoToCartAdapterImpl implements CartDtoToCartAdapter {
    private final CartItemDtoToCartItemAdapter cartItemDto;

    public CartDtoToCartAdapterImpl(CartItemDtoToCartItemAdapter cartItemDto) {
        this.cartItemDto = cartItemDto;
    }

    @Override
    public Cart convertToCartFromCartDto(CartDto cartDto) {
        try {
            // Map CartDto to Cart entity
            Cart cart = Cart.builder()
                    .userId(cartDto.getUserId() != null && !cartDto.getUserId().isEmpty()
                            ? cartDto.getUserId()
                            : null)
                    .build();

            // Map CartItemDto to CartItem and set to Cart
            if (cartDto.getCartItems() != null && !cartDto.getCartItems().isEmpty()) {
                cartDto.getCartItems().forEach(cartItemDto -> {
                    CartItem cartItem = this.cartItemDto.convertToCartItemFromCartItemDto(cartItemDto, cart);
                    if(cart.getCartItems() == null || cart.getCartItems().isEmpty()){
                        cart.setCartItems(new ArrayList<>());
                        cart.getCartItems().add(cartItem);
                    }
                    cart.getCartItems().add(cartItem);
                });
            }
            return cart;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert CartDto to Cart entity", e);
        }
    }
}
