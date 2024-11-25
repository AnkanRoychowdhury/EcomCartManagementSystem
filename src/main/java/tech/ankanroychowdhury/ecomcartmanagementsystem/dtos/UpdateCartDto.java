package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UpdateCartDto {
    String userId;
    List<UpdateCartItemDto> cartItems;
}
