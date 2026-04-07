package com.carbo.job.exception;

import com.carbo.job.model.Error.Error;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorException extends RuntimeException{
    private final Error error;
}
