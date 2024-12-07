package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart}
 */

@Builder
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
public class CartDto implements Serializable {

    String cartId;
    String userId;

    @Valid
    @NotNull
    @Size(message = "Cart must have at least one item", min = 1)
    transient List<CartItemDto> cartItems;
}