package com.example.aopdemo.exception;

public class RiskyOperationException extends RuntimeException {
    public RiskyOperationException(String message) {
        super(message);
    }
}
