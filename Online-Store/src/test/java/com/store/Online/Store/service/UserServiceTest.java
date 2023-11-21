package com.store.Online.Store.service;

import com.store.Online.Store.dto.AuthenticationRequest;
import com.store.Online.Store.dto.UserRequest;
import com.store.Online.Store.entity.Role;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.config.jwt.JwtTokenUtil;
import com.store.Online.Store.exception.RoleDefinitionException;
import com.store.Online.Store.exception.UserCreationException;
import com.store.Online.Store.exception.UserNotFoundException;
import com.store.Online.Store.repository.roleRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private userRepository userRepository;

    @Mock
    private roleRepository roleRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void register_ValidUserRequest_SuccessfulRegistration() {
        UserRequest userRequest = new UserRequest(
                "Artem",
                "John",
                "test@example.com",
                "password");
        Role userRole = new Role(1L, "USER");
        when(roleRepository.findRoleByRoleName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        userService.register(userRequest);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ThrowsUserCreationException() {
        UserRequest userRequest = new UserRequest(
                "Artem",
                "John",
                "test@example.com",
                "password");
        Role userRole = new Role(1L, "USER");
        when(roleRepository.findRoleByRoleName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(new User()));
        assertThrows(UserCreationException.class, () -> userService.register(userRequest));
    }

    @Test
    void register_InvalidEmailFormat_ThrowsIllegalArgumentException() {
        UserRequest userRequest = new UserRequest(
                "Artem",
                "John",
                "invalid-email",
                "password");
        assertThrows(IllegalArgumentException.class, () -> userService.register(userRequest));
    }

    @Test
    void register_RoleNotFound_ThrowsRoleNotFoundException(){
        UserRequest userRequest = new UserRequest(
                "Artem",
                "John",
                "test@example.com",
                "password");
        Role userRole = new Role("TEST");
        when(roleRepository.findRoleByRoleName(userRole.getRoleName())).thenReturn(Optional.empty());
        assertThrows(RoleDefinitionException.class, () -> userService.register(userRequest));
    }

    @Test
    void login_ValidAuthenticationRequest_ReturnsToken() {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "test@example.com",
                "Hash");
        User user = new User(
                "Artem",
                "John",
                "test@example.com",
                "Hash",
                new Role(2L, "USER"));
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(authRequest.getEmail(), user.getRoleId())).thenReturn("generatedToken");

        Map<String, String> response = userService.login(authRequest);

        assertEquals(user.getFirstName(), response.get("firstName"));
        assertEquals(user.getSecondName(), response.get("secondName"));
        assertEquals(user.getEmail(), response.get("email"));
        assertEquals("generatedToken", response.get("token"));
    }

    @Test
    void login_UserNotFound_ThrowsUserNotFoundException() {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "test@example.com",
                "password");
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.login(authRequest));
    }

    @Test
    void login_InvalidPassword_ThrowsBadCredentialsException() {
        AuthenticationRequest authRequest = new AuthenticationRequest(
                "test@example.com",
                "invalidPassword");
        User user = new User(
                "Artem",
                "John",
                "test@example.com",
                "Hash",
                new Role(2L, "USER"));
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> userService.login(authRequest));
    }
}
