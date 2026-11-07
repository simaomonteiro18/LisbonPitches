package com.simaomonteiro18.lisbonpitches.config;

import com.simaomonteiro18.lisbonpitches.services.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock JwtService jwtService;

    @Mock FilterChain chain;

    @InjectMocks JwtFilter jwtFilter;

    @AfterEach
    public void clean() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Token inválido não rebenta o filtro")
    public void invalidTokenDoesNotBreakFilter() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/pitches");
        request.addHeader("Authorization", "Bearer lixo");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUserId("lixo")).thenThrow(new MalformedJwtException("token invalido"));

        jwtFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test
    @DisplayName("Token vazio não rebenta o filtro")
    public void blankTokenDoesNotBreakFilter() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/pitches");
        request.addHeader("Authorization", "Bearer ");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUserId("")).thenThrow(new IllegalArgumentException("Token vazio"));

        jwtFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

}
