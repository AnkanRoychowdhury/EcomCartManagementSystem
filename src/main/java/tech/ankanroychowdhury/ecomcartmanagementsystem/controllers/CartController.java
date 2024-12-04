package tech.ankanroychowdhury.ecomcartmanagementsystem.controllers;

import com.sun.jdi.request.DuplicateRequestException;
import io.lettuce.core.RedisCommandExecutionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.ResponseDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.services.CartService;
import tech.ankanroychowdhury.ecomcartmanagementsystem.utils.ResponseBuilder;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_NOT_FOUND_MSG = "Cart not found";

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
        } catch (RedisCommandExecutionException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to merge cart", List.of(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDto<CartDto>> getCartById(@RequestParam String cartId) {
        try {
            CartDto guestCart = cartService.getCartFromRedis(cartId);
            CartDto cartDto = (guestCart != null) ? guestCart : cartService.getCartById(cartId);
            return ResponseBuilder.success("Cart retrieved successfully", cartDto);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving the cart", List.of(e.getMessage()));
        }
    }

    @PatchMapping("/items")
    public ResponseEntity<ResponseDto<CartDto>> addItemsToCart(@RequestParam String cartId, @Valid @RequestBody List<CartItemDto> cartItems) {
        try {
            CartDto updatedCart = cartService.addItemsToCart(cartId, cartItems);
            return ResponseBuilder.success("Successfully added items to cart", updatedCart);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while adding items to the cart", List.of(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteCart(@RequestParam String cartId) {
        try {
            cartService.deleteCart(cartId);
            return ResponseBuilder.success("Cart deleted successfully", null);
        } catch (EntityNotFoundException e) {
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
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(HttpStatus.NOT_FOUND, CART_NOT_FOUND_MSG, List.of(e.getMessage()));
        } catch (DuplicateRequestException e) {
            return ResponseBuilder.error(HttpStatus.ACCEPTED, "Nothing new to update", List.of(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Items info is invalid", List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating cart", List.of(e.getMessage()));
        }
    }
}
