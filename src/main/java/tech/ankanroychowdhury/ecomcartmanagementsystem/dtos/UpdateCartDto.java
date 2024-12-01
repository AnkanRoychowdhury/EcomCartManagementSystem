package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateCartDto implements Serializable {
    String userId;
    transient List<UpdateCartItemDto> cartItems;
}
