package tech.ankanroychowdhury.ecomcartmanagementsystem.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Cart", description = "Represents a Cart in the system")
public class Cart extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    private String userId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartItem> cartItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private CartMetadata metadata;

    @Column(nullable = false)
    private boolean active;
}
