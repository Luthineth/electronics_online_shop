package com.store.Online.Store.config;

import com.store.Online.Store.config.jwt.JwtAuthenticationFilter;
import com.store.Online.Store.config.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.nullValue;


public class JwtAuthenticationFilterTest {

    @Test
    void doFilter_ValidToken_ShouldSetAuthenticationInSecurityContext() throws ServletException, IOException {
        JwtTokenUtil jwtTokenUtil = mock(JwtTokenUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        String validToken = "validToken";
        when(jwtTokenUtil.resolveToken(request)).thenReturn(validToken);
        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(jwtTokenUtil.getAuthentication(validToken)).thenReturn(authentication);

        filter.doFilter(request, response, filterChain);

        verify(jwtTokenUtil).resolveToken(request);
        verify(jwtTokenUtil).validateToken(validToken);
        verify(jwtTokenUtil).getAuthentication(validToken);
        verify(filterChain).doFilter(request, response);

        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        assertThat(result, sameInstance(authentication));
    }

    @Test
    void doFilter_InvalidToken_ShouldNotSetAuthenticationInSecurityContext() throws ServletException, IOException {
        JwtTokenUtil jwtTokenUtil = mock(JwtTokenUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        String invalidToken = "invalidToken";
        when(jwtTokenUtil.resolveToken(request)).thenReturn(invalidToken);
        when(jwtTokenUtil.validateToken(invalidToken)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(jwtTokenUtil).resolveToken(request);
        verify(jwtTokenUtil).validateToken(invalidToken);
        verify(jwtTokenUtil, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);

        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        assertThat(result, nullValue());
    }
}
