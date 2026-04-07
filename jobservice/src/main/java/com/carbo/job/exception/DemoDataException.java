package com.carbo.job.exception;
public class DemoDataException extends RuntimeException {

    public DemoDataException() {
        super();
    }

    public DemoDataException(String message) {
        super(message);
    }

    public DemoDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DemoDataException(Throwable cause) {
        super(cause);
    }
}
