package tech.ankanroychowdhury.ecomcartmanagementsystem.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Schema(name = "CartItem", description = "Represents item in the cart")
public class CartItem extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Positive(message = "Price should not be lesser than 0")
    private double price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @Schema(hidden = true)
    private Cart cart;
}
