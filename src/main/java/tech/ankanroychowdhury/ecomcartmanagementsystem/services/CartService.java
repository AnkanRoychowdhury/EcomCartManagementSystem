package tech.ankanroychowdhury.ecomcartmanagementsystem.services;

import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;

import java.util.List;


public interface CartService {
    Cart saveCart(CartDto cartDto) throws Exception;
    CartDto getCartById(String cartId) throws Exception;
    CartDto addItemsToCart(String cartId, List<CartItemDto> cartItemsDto) throws Exception;
    void deleteCart(String cartId);
    CartDto updateCart(String cartId, UpdateCartDto cartDto) throws Exception;

    CartDto saveCartInRedis(CartDto cartDto) throws Exception;
    CartDto getCartFromRedis(String cartId) throws Exception;

}
