package com.simaomonteiro18.lisbonpitches.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;

    private final Cache<String, AtomicInteger> attempts = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip = getClientIp(request);

        AtomicInteger counter = attempts.getIfPresent(ip);

        if (counter != null && counter.get() >= MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setHeader("Retry-After", "900");
            response.setContentType("application/json");
            String body = """
        {"timeStamp":"%s","status":429,"error":"Too Many Requests.","message":"Demasiadas tentativas. Tenta novamente daqui a 15 minutos.","path":"%s"}
        """.formatted(Instant.now(), request.getRequestURI());

            response.getWriter().write(body);
            return;
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() == 401) {
            attempts.get(ip, k -> new AtomicInteger()).incrementAndGet();
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !("POST".equals(request.getMethod()) && "/login".equals(request.getRequestURI()));
    }

}
