package com.store.Online.Store.config;

import com.store.Online.Store.config.jwt.*;
import com.store.Online.Store.config.jwt.UserDetailsServiceImpl;
import com.store.Online.Store.entity.Role;
import com.store.Online.Store.exception.InvalidJwtTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenUtilTest {

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        jwtTokenUtil.setSecret("random_secret_key");
        jwtTokenUtil.setExpiration(60000L);
    }


    @Test
    void generateToken_Success() {
        Role role = new Role("USER");
        String email = "test@example.com";
        assertNotNull(jwtTokenUtil.generateToken(email, role));
    }

    @Test
    void getEmailFromToken_Success() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, new Role("USER"));
        String extractedEmail = jwtTokenUtil.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_Success() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email, new Role("USER"));
        boolean isValid = jwtTokenUtil.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ThrowsInvalidJwtTokenException() {
        String invalidToken = "invalid_token";
        assertThrows(InvalidJwtTokenException.class, () -> jwtTokenUtil.validateToken(invalidToken));
    }

    @Test
    void resolveToken_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        String resolvedToken = jwtTokenUtil.resolveToken(request);
        assertEquals("token123", resolvedToken);
    }

    @Test
    void resolveToken_WasFailed_ReturnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("boooooooooo");
        assertNull(jwtTokenUtil.resolveToken(request));
    }

    @Test
    void getAuthentication_Success() {
        String email = "test@example.com";
        Role role = new Role("USER");
        String token = jwtTokenUtil.generateToken(email, role);
        assertNotNull(token);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(role.getRoleName()));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "", authorities);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        Authentication authentication = jwtTokenUtil.getAuthentication(token);
        assertNotNull(authentication);
        assertEquals(email, ((UserDetails) authentication.getPrincipal()).getUsername());
    }
}
