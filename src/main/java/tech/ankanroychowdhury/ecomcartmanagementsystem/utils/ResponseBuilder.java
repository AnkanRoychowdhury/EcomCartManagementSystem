package tech.ankanroychowdhury.ecomcartmanagementsystem.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.ResponseDto;

import java.util.List;

public class ResponseBuilder {

    private ResponseBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> ResponseEntity<ResponseDto<T>> buildResponse(
            HttpStatus status, String message, T data, List<String> errors) {
        ResponseDto<T> response = ResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ResponseDto<T>> success(String message, T data) {
        return buildResponse(HttpStatus.OK, message, data, null);
    }

    public static <T> ResponseEntity<ResponseDto<T>> error(HttpStatus status, String message, List<String> errors) {
        return buildResponse(status, message, null, errors);
    }
}

