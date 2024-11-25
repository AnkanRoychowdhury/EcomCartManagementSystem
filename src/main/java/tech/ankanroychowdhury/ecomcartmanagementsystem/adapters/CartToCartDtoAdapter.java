package tech.ankanroychowdhury.ecomcartmanagementsystem.adapters;

import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;

public interface CartToCartDtoAdapter {
    CartDto convertToCartDto(Cart cart);
}
