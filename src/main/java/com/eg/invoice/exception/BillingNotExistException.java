package com.eg.invoice.exception;

public class BillingNotExistException extends RuntimeException{
    public BillingNotExistException(String message) {
        super(message);
    }
}
