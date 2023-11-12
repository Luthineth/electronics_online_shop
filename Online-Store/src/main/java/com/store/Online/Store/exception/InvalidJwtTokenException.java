package com.store.Online.Store.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtTokenException extends AuthenticationException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
