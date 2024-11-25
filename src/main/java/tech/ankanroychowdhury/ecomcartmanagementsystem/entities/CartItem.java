package tech.ankanroychowdhury.ecomcartmanagementsystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Builder
@NoArgsConstructor
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class CartItem {
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
    private Cart cart;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
}
