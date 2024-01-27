package com.nextgendynamics.crm.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleException(IllegalStateException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleException() {
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<?> handleException(InvalidObjectException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getErrorMessages());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleException(DataIntegrityViolationException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleException2(Exception exp) {
        String txt1 = "  ";
        return ResponseEntity
                .badRequest()
                .body("Missing body Object! Error:" + exp.getMessage() );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException3(RuntimeException exp) {
        String txt1 = "  ";
        if ( exp.getMessage().contains("Required request body is missing")){
            return ResponseEntity
                    .badRequest()
                    .body( "Please make sure the request is sent with the JSON object in the body!"+
                            "\nError Message: " + exp.getMessage() );
        } else {
            return ResponseEntity
                    .badRequest()
                    .body("\nError Message: " + exp.getMessage());
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleException4(ResourceNotFoundException exp) {
        String txt1 = exp.getMessage();
        return ResponseEntity
                .badRequest()
                .body("Probable insufficient privilege ! Error:" + exp.getMessage() );
    }

}

