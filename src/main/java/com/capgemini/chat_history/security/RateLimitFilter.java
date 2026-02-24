package com.capgemini.chat_history.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Stores buckets per API Key
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Define limit: 50 requests per minute
        Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        // Skip rate limiting if API key is missing (Filter will handle auth separately)
        if (apiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(apiKey, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Retry-After-Seconds",
                    String.valueOf(bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill() / 1_000_000_000));
            response.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }
}
