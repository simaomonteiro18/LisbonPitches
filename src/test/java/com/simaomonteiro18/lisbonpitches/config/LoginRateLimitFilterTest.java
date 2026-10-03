package com.simaomonteiro18.lisbonpitches.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRateLimitFilterTest {

    private LoginRateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new LoginRateLimitFilter();
    }

    @Test
    @DisplayName("Teste ao retorno de LoginRateLimitFilter após 5 tentativas")
    public void loginRateLimitAfterFifthAttempt() throws Exception {

        AtomicBoolean chainCalled = new AtomicBoolean(false);

        FilterChain chain = (req, res) -> {
            chainCalled.set(true);
            ((HttpServletResponse) res).setStatus(401);
        };

        for (int i = 0; i < 5; i++) {

            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);

            assertEquals(401, response.getStatus());

        }

        chainCalled.set(false);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);

        assertEquals(429, response.getStatus());
        assertEquals("900", response.getHeader("Retry-After"));
        assertFalse(chainCalled.get());

    }

    private MockHttpServletResponse sendLogin(String ip, FilterChain chain) throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        request.addHeader("X-Forwarded-For", ip);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;

    }

    @Test
    @DisplayName("Um IP bloqueado não afeta outro IP")
    public void blockedIpDoesNotAffectOtherIp() throws Exception {

        AtomicBoolean chainCalled = new AtomicBoolean(false);

        FilterChain chain = (req, res) -> {
            chainCalled.set(true);
            ((HttpServletResponse) res).setStatus(401);
        };

        for (int i = 0; i < 5; i++) {
            sendLogin("1.1.1.1", chain);
        }

        MockHttpServletResponse responseSixthAttemptIpA = sendLogin("1.1.1.1", chain);

        chainCalled.set(false);

        MockHttpServletResponse responseIpB = sendLogin("2.2.2.2", chain);

        assertEquals(429, responseSixthAttemptIpA.getStatus());
        assertEquals(401, responseIpB.getStatus());
        assertTrue(chainCalled.get());

    }

    @Test
    @DisplayName("Pedidos que não são POST /login passam mesmo com o IP bloqueado")
    public void nonLoginRequestsPassWhenIpIsBlocked() throws Exception {

        FilterChain chain = (req, res) -> {
            ((HttpServletResponse) res).setStatus(401);
        };

        for (int i = 0; i < 5; i++) {
            sendLogin("3.3.3.3", chain);
        }

        assertEquals(429, sendLogin("3.3.3.3", chain).getStatus());

        MockHttpServletRequest requestGet = new MockHttpServletRequest("GET", "/login");
        MockHttpServletRequest requestPost = new MockHttpServletRequest("POST", "/pitches");

        requestGet.addHeader("X-Forwarded-For", "3.3.3.3");
        requestPost.addHeader("X-Forwarded-For", "3.3.3.3");

        MockHttpServletResponse responseGet = new MockHttpServletResponse();
        MockHttpServletResponse responsePost = new MockHttpServletResponse();

        filter.doFilter(requestGet, responseGet, chain);
        filter.doFilter(requestPost, responsePost, chain);

        assertEquals(401, responseGet.getStatus());
        assertEquals(401, responsePost.getStatus());

    }

}