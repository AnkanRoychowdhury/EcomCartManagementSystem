package tech.ankanroychowdhury.ecomcartmanagementsystem.adapters;

import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.CartItem;

import java.util.List;

public interface CartItemDtoToCartItemAdapter {
    CartItem convertToCartItemFromCartItemDto(CartItemDto cartItemDto, Cart cart);

    List<CartItem> convertToCartItemListFromCartItemsDto(List<CartItemDto> cartItemsDto, Cart cart);

    List<CartItem> fromUpdateCartItemDto(List<UpdateCartItemDto> updateCartItemsDto, Cart cart);
}
