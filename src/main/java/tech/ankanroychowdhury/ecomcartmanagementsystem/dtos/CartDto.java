package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart}
 */
@Value
@Builder
public class CartDto implements Serializable {

    String cartId;
    String userId;

    @Valid
    @NotNull
    @Size(message = "Cart must have at least one item", min = 1)
    List<CartItemDto> cartItems;
    Date createdAt;
    Date updatedAt;
}