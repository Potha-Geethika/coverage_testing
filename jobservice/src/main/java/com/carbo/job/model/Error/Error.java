package com.carbo.job.model.Error;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class Error {

    private String errorCode;

    private String errorMessage;

    private HttpStatus httpStatus;
}
