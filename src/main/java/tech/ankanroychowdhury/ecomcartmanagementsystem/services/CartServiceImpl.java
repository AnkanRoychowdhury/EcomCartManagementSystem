package tech.ankanroychowdhury.ecomcartmanagementsystem.services;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tech.ankanroychowdhury.ecomcartmanagementsystem.adapters.CartDtoToCartAdapter;
import tech.ankanroychowdhury.ecomcartmanagementsystem.adapters.CartToCartDtoAdapter;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.adapters.CartItemDtoToCartItemAdapter;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.CartItem;
import tech.ankanroychowdhury.ecomcartmanagementsystem.repositories.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartDtoToCartAdapter cartAdapter;
    private final CartToCartDtoAdapter cartToCartDtoAdapter;
    private final CartItemDtoToCartItemAdapter cartItemDtoToCartItemAdapter;
    private Boolean isUpdated = false;

    public CartServiceImpl(CartRepository cartRepository,
                           CartDtoToCartAdapter cartAdapter,
                           CartToCartDtoAdapter cartToCartDtoAdapter,
                           CartItemDtoToCartItemAdapter cartItemDtoToCartItemAdapter
    ) {
        this.cartRepository = cartRepository;
        this.cartAdapter = cartAdapter;
        this.cartToCartDtoAdapter = cartToCartDtoAdapter;
        this.cartItemDtoToCartItemAdapter = cartItemDtoToCartItemAdapter;
    }

    @Override
    public Cart saveCart(CartDto cartDto) throws Exception {
        try {
            Cart cart = this.cartAdapter.convertToCartFromCartDto(cartDto);
            return this.cartRepository.save(cart);
        }catch(Exception e) {
            e.printStackTrace();
            throw new Exception(
                    e.getMessage(),
                    e.getCause()
            );
        }
    }

    @Override
    public CartDto getCartById(String cartId) {
        Cart cart = findCartById(cartId);
        return this.cartToCartDtoAdapter.convertToCartDto(cart);
    }

    @Override
    public CartDto addItemsToCart(String cartId, List<CartItemDto> cartItemsDto) throws Exception {
        try {
            Cart cart = findCartById(cartId);
            List<CartItem> cartItems = this.cartItemDtoToCartItemAdapter.convertToCartItemListFromCartItemsDto(cartItemsDto, cart);
            cart.getCartItems().addAll(cartItems);
            return this.cartToCartDtoAdapter.convertToCartDto(this.cartRepository.save(cart));
        }catch(Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void deleteCart(String cartId) {
        Cart cart = findCartById(cartId);
        this.cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public CartDto updateCart(String cartId, UpdateCartDto updateCartDto) throws Exception {
        Cart existingCart = findCartById(cartId);
        String userIdToUpdate = updateCartDto.getUserId();
        if(userIdToUpdate != null && !existingCart.getUserId().equals(userIdToUpdate)){
            existingCart.setUserId(userIdToUpdate);
            isUpdated = true;
        }
        List<UpdateCartItemDto> updateCartItemDto = updateCartDto.getCartItems();
        List<CartItem> existingCartCartItems = existingCart.getCartItems();
        List<CartItem> toUpdateCartItems = this.cartItemDtoToCartItemAdapter.fromUpdateCartItemDto(updateCartItemDto, existingCart);
        List<CartItem> updatedCartItems = matchAndUpdateExistingCartItems(toUpdateCartItems, existingCartCartItems);
        if(!isUpdated) throw new DuplicateRequestException("Cart is already updated");
        existingCart.setCartItems(updatedCartItems);
        Cart cart = this.cartRepository.save(existingCart);
        return this.cartToCartDtoAdapter.convertToCartDto(cart);
    }

    private List<CartItem> matchAndUpdateExistingCartItems(List<CartItem> toUpdateCartItems, List<CartItem> existingCartItems) {
        // Create a map for quick lookup of existing items by productId
        Map<String, CartItem> existingItemsMap = existingCartItems.stream()
                .collect(Collectors.toMap(CartItem::getProductId, item -> item));

        // Iterate through the toUpdateCartItems
        for (CartItem updateItem : toUpdateCartItems) {
            String productId = updateItem.getProductId();

            // If the product exists in the current cart, update it
            if (existingItemsMap.containsKey(productId)) {
                CartItem existingItem = existingItemsMap.get(productId);

                // Update quantity if different
                if (updateItem.getQuantity() > 0 && updateItem.getQuantity() != existingItem.getQuantity()) {
                    existingItem.setQuantity(updateItem.getQuantity());
                    isUpdated = true;
                }

                // Update price if different
                if (updateItem.getPrice() > 0.0 && Double.compare(updateItem.getPrice(), existingItem.getPrice()) != 0) {
                    existingItem.setPrice(updateItem.getPrice());
                    isUpdated = true;
                }
            } else {
                // If the product is new, add it to the map
                existingItemsMap.put(productId, updateItem);
                isUpdated = true;
            }
        }
        // Return the updated list of items
        return new ArrayList<>(existingItemsMap.values());
    }

    private Cart findCartById(String cartId) {
        Cart cart = this.cartRepository.findCartByCartId(cartId);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found with ID: " + cartId);
        }
        return cart;
    }
}