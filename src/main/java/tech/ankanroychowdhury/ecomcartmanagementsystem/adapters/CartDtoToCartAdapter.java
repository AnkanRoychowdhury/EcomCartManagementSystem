package tech.ankanroychowdhury.ecomcartmanagementsystem.adapters;

import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;

public interface CartDtoToCartAdapter {
    public Cart convertToCartFromCartDto(CartDto cartDto);
}
