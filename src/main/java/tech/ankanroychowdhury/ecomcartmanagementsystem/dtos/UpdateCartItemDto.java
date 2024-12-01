package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateCartItemDto implements Serializable {
    String productId;

    @Min(message = "Minimum Quantity should be at least 1", value = 1)
    @Max(message = "You can add max of 5 products at a time", value = 5)
    @Positive(message = "Quantity should not be lesser than 1")
    int quantity;

    @Min(message = "Minimum Price should be at least 0", value = 0)
    @Positive(message = "Price should not be in negative")
    double price;
}
