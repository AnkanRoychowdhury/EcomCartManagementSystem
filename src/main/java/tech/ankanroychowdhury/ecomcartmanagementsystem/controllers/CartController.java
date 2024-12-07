package tech.ankanroychowdhury.ecomcartmanagementsystem.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jdi.request.DuplicateRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.ResponseDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.CartNotFoundException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.InvalidCartOperationException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions.RedisOperationException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.services.CartService;
import tech.ankanroychowdhury.ecomcartmanagementsystem.utils.ResponseBuilder;

import java.util.List;


@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Management", description = "Operations related to managing user carts")
public class CartController {

    private final CartService cartService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_NOT_FOUND_MSG = "Cart not found";
    private static final String REDIS_GENERAL_ERROR_MSG = "Redis operation failed";

    public CartController(CartService cartService, RedisTemplate<String, Object> redisTemplate) {
        this.cartService = cartService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CartDto>> saveCart(@Valid @RequestBody CartDto cartDto) {
        try {
            CartDto savedCart = (cartDto.getUserId() == null || cartDto.getUserId().isEmpty())
                    ? cartService.saveCartInRedis(cartDto)
                    : cartService.saveCart(cartDto);
            return ResponseBuilder.success("Cart created successfully", savedCart);
        } catch (RedisOperationException e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, REDIS_GENERAL_ERROR_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create cart", List.of(e.getMessage()));
        }
    }

    @PostMapping("/merge/{userId}")
    public ResponseEntity<ResponseDto<CartDto>> mergeCart(@RequestParam String cartId, @PathVariable String userId) {
        try {
            CartDto guestCart = cartService.getCartFromRedis(cartId);
            guestCart.setUserId(userId);
            CartDto mergedCart = cartService.saveCart(guestCart);
            redisTemplate.opsForValue().getAndDelete("cart:guest:" + cartId);
            return ResponseBuilder.success("Successfully merged cart", mergedCart);
        } catch (CartNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (RedisOperationException e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, REDIS_GENERAL_ERROR_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to merge cart", List.of(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDto<CartDto>> getCartById(@RequestParam String cartId) {
        try {
            CartDto cartDto = findCartByIdFromRedisOrDb(cartId);
            return ResponseBuilder.success("Cart retrieved successfully", cartDto);
        } catch (CartNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (RedisOperationException e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, REDIS_GENERAL_ERROR_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving the cart", List.of(e.getMessage()));
        }
    }

    private CartDto findCartByIdFromRedisOrDb(String cartId) throws CartNotFoundException, RedisOperationException, JsonProcessingException {
        // Attempt to fetch from Redis
        CartDto redisCart = this.cartService.getCartFromRedis(cartId);
        // If Redis fetch fails or returns null, fetch from DB
        if (redisCart == null) {
            return fetchCartFromDb(cartId);
        }
        return redisCart;
    }

    private CartDto fetchCartFromDb(String cartId) throws CartNotFoundException {
        try {
            return cartService.getCartById(cartId);
        } catch (EntityNotFoundException e) {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found in the database");
        }
    }


    @PatchMapping("/items")
    public ResponseEntity<ResponseDto<CartDto>> addItemsToCart(@RequestParam String cartId, @Valid @RequestBody List<CartItemDto> cartItems) {
        try {
            CartDto updatedCart = cartService.addItemsToCart(cartId, cartItems);
            return ResponseBuilder.success("Successfully added items to cart", updatedCart);
        } catch (CartNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (InvalidCartOperationException e) {
            return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid cart operation", List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while adding items to the cart", List.of(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteCart(@RequestParam String cartId) {
        try {
            cartService.deleteCart(cartId);
            return ResponseBuilder.success("Cart deleted successfully", null);
        } catch (CartNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the cart", List.of(e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDto<CartDto>> updateCart(@RequestParam String cartId, @Valid @RequestBody UpdateCartDto updateCartDto) {
        try {
            CartDto updatedCart = cartService.updateCart(cartId, updateCartDto);
            return ResponseBuilder.success("Successfully updated the cart", updatedCart);
        } catch (CartNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (DuplicateRequestException e) {
            return ResponseBuilder.error(HttpStatus.ACCEPTED, "Nothing new to update", List.of(e.getMessage()));
        } catch (InvalidCartOperationException e) {
            return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid cart operation", List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating cart", List.of(e.getMessage()));
        }
    }
}