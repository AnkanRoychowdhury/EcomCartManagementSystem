package tech.ankanroychowdhury.ecomcartmanagementsystem.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.request.DuplicateRequestException;
import io.lettuce.core.RedisCommandExecutionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
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
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.CartNotFoundException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.RedisOperationException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.repositories.CartRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartDtoToCartAdapter cartAdapter;
    private final CartToCartDtoAdapter cartToCartDtoAdapter;
    private final CartItemDtoToCartItemAdapter cartItemDtoToCartItemAdapter;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartDtoToCartAdapter cartAdapter,
                           CartToCartDtoAdapter cartToCartDtoAdapter,
                           CartItemDtoToCartItemAdapter cartItemDtoToCartItemAdapter,
                           RedisTemplate<String, Object> redisTemplate,
                           ObjectMapper objectMapper) {
        this.cartRepository = cartRepository;
        this.cartAdapter = cartAdapter;
        this.cartToCartDtoAdapter = cartToCartDtoAdapter;
        this.cartItemDtoToCartItemAdapter = cartItemDtoToCartItemAdapter;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CartDto saveCart(CartDto cartDto) {
        Cart cart = this.cartAdapter.convertToCartFromCartDto(cartDto);
        return this.cartToCartDtoAdapter.convertToCartDto(cartRepository.save(cart));
    }

    @Override
    public CartDto getCartById(String cartId) {
        return cartToCartDtoAdapter.convertToCartDto(findCartById(cartId));
    }

    @Override
    public CartDto addItemsToCart(String cartId, List<CartItemDto> cartItemsDto) {
        Cart cart = findCartById(cartId);
        List<CartItem> cartItems = cartItemDtoToCartItemAdapter.convertToCartItemListFromCartItemsDto(cartItemsDto, cart);
        cart.getCartItems().addAll(cartItems);
        return cartToCartDtoAdapter.convertToCartDto(cartRepository.save(cart));
    }

    @Override
    public void deleteCart(String cartId) {
        Cart cart = findCartById(cartId);
        cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public CartDto updateCart(String cartId, UpdateCartDto updateCartDto) {
        Cart existingCart = findCartById(cartId);
        boolean isUpdated = updateCartFields(existingCart, updateCartDto);
        if (!isUpdated) throw new DuplicateRequestException("Cart is already updated");
        Cart updatedCart = cartRepository.save(existingCart);
        return cartToCartDtoAdapter.convertToCartDto(updatedCart);
    }

    @Override
    public CartDto saveCartInRedis(CartDto cartDto) {
        String cartId = generateCartId();
        String cartKey = generateRedisKey(cartId);
        cartDto.setCartId(cartId);
        saveToRedis(cartKey, cartDto);
        return cartDto;
    }

    @Override
    public CartDto getCartFromRedis(String cartId) throws RedisOperationException, JsonProcessingException{
        String cartKey = generateRedisKey(cartId);
        return retrieveFromRedis(cartKey);
    }

    // --- PRIVATE METHODS ---
    private Cart findCartById(String cartId) {
        Cart cart = this.cartRepository.findCartByCartId(cartId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found with ID: " + cartId);
        }
        return cart;
    }

    private boolean updateCartFields(Cart existingCart, UpdateCartDto updateCartDto) {
        boolean isUpdated = false;

        // Update User ID
        if (updateCartDto.getUserId() != null && !updateCartDto.getUserId().equals(existingCart.getUserId())) {
            existingCart.setUserId(updateCartDto.getUserId());
            isUpdated = true;
        }

        // Update Cart Items
        List<CartItem> toUpdateCartItems = cartItemDtoToCartItemAdapter.fromUpdateCartItemDto(updateCartDto.getCartItems(), existingCart);
        isUpdated |= updateCartItems(existingCart.getCartItems(), toUpdateCartItems);

        return isUpdated;
    }

    private boolean updateCartItems(List<CartItem> existingCartItems, List<CartItem> toUpdateCartItems) {
        Map<String, CartItem> existingItemsMap = existingCartItems.stream()
                .collect(Collectors.toMap(CartItem::getProductId, item -> item));

        boolean isUpdated = false;

        for (CartItem updateItem : toUpdateCartItems) {
            CartItem existingItem = existingItemsMap.get(updateItem.getProductId());

            if (existingItem != null) {
                // Update existing item
                if (updateItem.getQuantity() != existingItem.getQuantity()) {
                    existingItem.setQuantity(updateItem.getQuantity());
                    isUpdated = true;
                }
                if (updateItem.getPrice()!= existingItem.getPrice()) {
                    existingItem.setPrice(updateItem.getPrice());
                    isUpdated = true;
                }
            } else {
                // Add new item
                existingItemsMap.put(updateItem.getProductId(), updateItem);
                isUpdated = true;
            }
        }

        existingCartItems.clear();
        existingCartItems.addAll(existingItemsMap.values());
        return isUpdated;
    }

    private void saveToRedis(String key, CartDto cartDto) {
        try {
            String serializedCart = objectMapper.writeValueAsString(cartDto);
            redisTemplate.opsForValue().set(key, serializedCart, Duration.ofHours(1));
        } catch (Exception e) {
            throw new RedisOperationException("Error saving cart to Redis", e);
        }
    }

    private CartDto retrieveFromRedis(String key) throws JsonProcessingException {
        String serializedCart = (String) redisTemplate.opsForValue().get(key);
        if (serializedCart == null) {
            throw new CartNotFoundException("Cart not found in Redis with key: " + key);
        }
        return objectMapper.readValue(serializedCart, CartDto.class);
    }

    private String generateRedisKey(String cartId) {
        return "cart:guest:" + (cartId != null ? cartId : UUID.randomUUID());
    }

    private String generateCartId() {
        return UUID.randomUUID().toString();
    }
}
