package tech.ankanroychowdhury.ecomcartmanagementsystem.controllers;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.ankanroychowdhury.ecomcartmanagementsystem.adapters.CartToCartDtoAdapter;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartItemDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.ResponseDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.UpdateCartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.services.CartService;
import java.util.List;


@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;
    private final CartToCartDtoAdapter cartToCartDtoAdapter;
    private static final String CART_NOT_FOUND_MSG = "Cart not found";


    public CartController(CartService cartService, CartToCartDtoAdapter cartToCartDtoAdapter) {
        this.cartService = cartService;
        this.cartToCartDtoAdapter = cartToCartDtoAdapter;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CartDto>> saveCart(@Valid @RequestBody CartDto cartDto) {
        try {
            CartDto savedCart;
            if(cartDto.getUserId() == null) {
                savedCart = this.cartService.saveCartInRedis(cartDto);
            }else {
                savedCart = this.cartToCartDtoAdapter.convertToCartDto(this.cartService.saveCart(cartDto));
            }
            return new ResponseEntity<>(
                    ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Cart created successfully")
                    .data(savedCart)
                    .errors(null)
                    .build(),
                    HttpStatus.OK
            );
        }catch(Exception e) {
            return new ResponseEntity<>(
                    ResponseDto.<CartDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Unable to create cart")
                            .data(null)
                            .errors(List.of(e.getMessage()))
                            .build(),
                    HttpStatus.OK
            );
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDto<CartDto>> getCartById(@RequestParam String cartId) throws Exception {
        try {

            // fetch cart from redis for guest user
            CartDto guestCart = this.cartService.getCartFromRedis(cartId);
            // Fetch the cart by ID
            CartDto cartDto = guestCart != null ? guestCart : this.cartService.getCartById(cartId);

            // Build success response
            ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Cart retrieved successfully")
                    .data(cartDto)
                    .build();

            return ResponseEntity.ok(response);
        }
        catch(RedisSystemException e){
            CartDto cartDto = this.cartService.getCartById(cartId);
            ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Cart retrieved successfully")
                    .data(cartDto)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (EntityNotFoundException e) {
            // Handle exceptions (e.g., cart not found)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(CART_NOT_FOUND_MSG)
                    .errors(List.of(e.getMessage()))
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (Exception e) {
            // Handle general exceptions
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred while retrieving the cart")
                    .errors(List.of(e.getMessage()))
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/items")
    public ResponseEntity<ResponseDto<CartDto>> addItemsToCart(@RequestParam String cartId, @Valid @RequestBody List<CartItemDto> cartItems) {
        try {
            CartDto cartDto = this.cartService.addItemsToCart(cartId, cartItems);
            ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Successfully Added items into the cart")
                    .data(cartDto)
                    .build();
            return ResponseEntity.ok(response);
        }
        catch (EntityNotFoundException e) {
            // Handle exceptions (e.g., cart not found)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(CART_NOT_FOUND_MSG)
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (Exception e) {
            // Handle general exceptions
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred while adding items into the cart")
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<CartDto>> deleteCart(@RequestParam String cartId) {
        try {
            // Fetch the cart by ID
            this.cartService.deleteCart(cartId);
            // Build success response
            ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Cart deleted successfully")
                    .build();
            return ResponseEntity.ok(response);
        }
        catch (EntityNotFoundException e) {
            // Handle exceptions (e.g., cart not found)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(CART_NOT_FOUND_MSG)
                    .errors(List.of(e.getMessage()))
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (Exception e) {
            // Handle general exceptions
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred while deleting the cart")
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDto<CartDto>> updateCart(@RequestParam String cartId, @Valid @RequestBody UpdateCartDto updateCartDto){
        try {
            CartDto cartDto = this.cartService.updateCart(cartId, updateCartDto);
            ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.OK)
                    .message("Successfully updated the cart")
                    .data(cartDto)
                    .build();
            return ResponseEntity.ok(response);
        }
        catch (EntityNotFoundException e) {
            // Handle exceptions (e.g., cart not found)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(CART_NOT_FOUND_MSG)
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (DuplicateRequestException e) {
            // Handle exceptions (e.g., cart not found)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.ACCEPTED)
                    .message("Nothing new to update")
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse);
        }
        catch (IllegalArgumentException e) {
            // Handle exceptions (e.g., Bad request)
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Items info is invalid")
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception e) {
            // Handle general exceptions
            ResponseDto<CartDto> errorResponse = ResponseDto.<CartDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred while updating cart")
                    .errors(List.of(e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
