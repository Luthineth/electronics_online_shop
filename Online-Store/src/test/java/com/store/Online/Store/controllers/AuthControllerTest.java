package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.AuthenticationRequest;
import com.store.Online.Store.dto.UserRequest;
import com.store.Online.Store.exception.RoleDefinitionException;
import com.store.Online.Store.exception.UserCreationException;
import com.store.Online.Store.exception.UserNotFoundException;
import com.store.Online.Store.service.userService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private userService userService;

    @InjectMocks
    private AuthController authController;

    UserRequest userRequest = new UserRequest(
            "Artem",
            "Lihachev",
            "sofaross228@gmail.com",
            "123321"
    );

    AuthenticationRequest authenticationRequest = new AuthenticationRequest(
            "sofaross228@gmail.com",
            "123321"
            );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuthenticationToken_ValidUserRequest_ReturnsHttpStatusCreated() {
        doNothing().when(userService).register(userRequest);
        ResponseEntity<?> response = authController.createAuthenticationToken(userRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).register(userRequest);
    }

    @Test
    void createAuthenticationToken_InvalidUserRequest_ReturnsHttpStatusBadRequest() {
        doThrow(IllegalArgumentException.class).when(userService).register(userRequest);
        ResponseEntity<?> response = authController.createAuthenticationToken(userRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, times(1)).register(userRequest);
    }

    @Test
    void createAuthenticationToken_UserCreationException_ReturnsHttpStatusConflict() {
        doThrow(UserCreationException.class).when(userService).register(userRequest);
        ResponseEntity<?> response = authController.createAuthenticationToken(userRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userService, times(1)).register(userRequest);
    }

    @Test
    void createAuthenticationToken_RoleDefinitionException_ReturnsHttpStatusInternalServerError() {
        doThrow(RoleDefinitionException.class).when(userService).register(userRequest);
        ResponseEntity<?> response = authController.createAuthenticationToken(userRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService, times(1)).register(userRequest);
    }

    @Test
    void authenticateUser_ValidAuthenticationRequest_ReturnsHttpStatusOk() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("token", "testToken");
        when(userService.login(any(AuthenticationRequest.class))).thenReturn(responseMap);
        ResponseEntity<?> response = authController.authenticateUser(authenticationRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseMap, response.getBody());
        verify(userService, times(1)).login(authenticationRequest);
    }

    @Test
    void authenticateUser_UserNotFoundException_ReturnsHttpStatusNOT_FOUND() {
        when(userService.login(any(AuthenticationRequest.class))).thenThrow(UserNotFoundException.class);
        ResponseEntity<?> response = authController.authenticateUser(authenticationRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).login(authenticationRequest);
    }

    @Test
    void authenticateUser_BadCredentialsException_ReturnsHttpStatusUNAUTHORIZED() {
        when(userService.login(any(AuthenticationRequest.class))).thenThrow(BadCredentialsException.class);
        ResponseEntity<?> response = authController.authenticateUser(authenticationRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService, times(1)).login(authenticationRequest);
    }
}