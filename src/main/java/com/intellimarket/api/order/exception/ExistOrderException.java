package com.intellimarket.api.order.exception;

public class ExistOrderException extends RuntimeException {
    public ExistOrderException(String message) {
        super(message);
    }
}
