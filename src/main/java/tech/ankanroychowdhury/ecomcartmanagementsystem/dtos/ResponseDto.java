package tech.ankanroychowdhury.ecomcartmanagementsystem.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto<T> implements Serializable {
    private HttpStatus status;
    private String message;
    private transient T data;
    private List<String> errors;
}
