package com.store.Online.Store.exception;

public class InvalidJwtTokenException extends RuntimeException{
    public InvalidJwtTokenException(String message) { super(message);
    }
}
