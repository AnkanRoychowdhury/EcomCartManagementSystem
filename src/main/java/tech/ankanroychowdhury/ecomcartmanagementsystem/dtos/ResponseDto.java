package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(hidden = true)
public class ResponseDto<T> implements Serializable {
    private HttpStatus status;

    private String message;

    private transient T data;

    private List<String> errors;
}
