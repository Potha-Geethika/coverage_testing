
package com.carbo.job.exception;

import com.carbo.job.model.Error.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends Throwable {
    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<Error> customExceptionHandler(ErrorException ex) {
        log.error("Exception occurred: {}", ex.getMessage());
        return ResponseEntity.status(ex.getError().getHttpStatus()).contentType(MediaType.APPLICATION_JSON).body(ex.getError());
    }
}
