package tech.ankanroychowdhury.ecomcartmanagementsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.ResponseDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.utils.ResponseBuilder;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleCartNotFoundException(CartNotFoundException ex) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND, "Cart not found", List.of(ex.getMessage()));
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ResponseDto<Void>> handleRedisOperationException(RedisOperationException ex) {
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Redis operation failed", List.of(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ResponseDto<Void>> handleDuplicateRequestException(DuplicateRequestException ex) {
        return ResponseBuilder.error(HttpStatus.ACCEPTED, "Nothing new to update", List.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCartOperationException.class)
    public ResponseEntity<ResponseDto<Void>> handleInvalidCartOperationException(InvalidCartOperationException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid cart operation", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodArgumentMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid argument type", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodArgumentMismatchException(MethodArgumentNotValidException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid arguments, Validation failed", List.of(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Void>> handleUnrecognizedFieldException(HttpMessageNotReadableException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid field", List.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", List.of(ex.getMessage()));
    }
}
