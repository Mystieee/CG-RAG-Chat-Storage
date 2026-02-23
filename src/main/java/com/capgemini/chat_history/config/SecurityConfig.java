package com.capgemini.chat_history.config;

import org.springframework.context.annotation.Configuration;
import com.capgemini.chat_history.security.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>(filter);
        // Ensure this filter runs for all /api paths
        registration.addUrlPatterns("/api/*");
        // Ensure it runs before other logic
        registration.setOrder(1);
        return registration;
    }
}
