package tech.ankanroychowdhury.ecomcartmanagementsystem.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CartMetadata", description = "Holds metadata for a Cart")
public class CartMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private String ipAddress;

    @Lob
    private String additionalInfo;
}
