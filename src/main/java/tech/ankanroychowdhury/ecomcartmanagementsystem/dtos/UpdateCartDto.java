package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
public class UpdateCartDto implements Serializable {
    String userId;
    transient List<UpdateCartItemDto> cartItems;
}
