package tech.ankanroychowdhury.ecomcartmanagementsystem.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.CartNotFoundException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.CartOperationException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.DuplicateRequestException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.RedisOperationException;

import java.util.List;


public interface CartService {
    CartDto saveCart(CartDto cartDto) throws CartOperationException;
    CartDto getCartById(String cartId) throws CartNotFoundException;
    CartDto addItemsToCart(String cartId, List<CartItemDto> cartItemsDto) throws CartNotFoundException, CartOperationException;
    void deleteCart(String cartId) throws CartNotFoundException;
    CartDto updateCart(String cartId, UpdateCartDto updateCartDto) throws CartNotFoundException, DuplicateRequestException, CartOperationException;

    // Redis-specific operations
    CartDto saveCartInRedis(CartDto cartDto) throws RedisOperationException;
    CartDto getCartFromRedis(String cartId) throws CartNotFoundException, RedisOperationException, JsonProcessingException;
}
