package com.security.chassi.handler;

import com.security.chassi.dtos.ErrorResponseDTO;
import com.security.chassi.dtos.ValidationErrorResponseDTO;
import com.security.chassi.exceptions.*;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            TokenExpiredException.class,
            InvalidTokenException.class,
            InvalidRefreshTokenException.class,
            InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(RuntimeException ex) {
        ErrorResponseDTO error = buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ErrorResponseDTO error = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO error = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> {
                            String message = fieldError.getDefaultMessage();
                            return message != null ? message : "Erro de validação";
                        },
                        (msg1, msg2) -> msg1
                ));

        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    private ErrorResponseDTO buildErrorResponse(HttpStatus status, String message) {
        return new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }
}
