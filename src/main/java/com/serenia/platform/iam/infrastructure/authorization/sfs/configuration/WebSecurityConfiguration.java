package com.serenia.platform.iam.infrastructure.authorization.sfs.configuration;

import com.serenia.platform.iam.domain.services.SessionQueryService;
import com.serenia.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.serenia.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Spring Security configuration of the platform.
 *
 * <p>Sets up a stateless pipeline: registration, sign-in and the API documentation are
 * public; every other endpoint requires a bearer token validated by
 * {@link BearerAuthorizationRequestFilter}.</p>
 */
@Configuration
public class WebSecurityConfiguration {

    private final BearerTokenService tokenService;
    private final SessionQueryService sessionQueryService;
    private final AuthenticationEntryPoint unauthorizedRequestHandler;

    public WebSecurityConfiguration(BearerTokenService tokenService,
                                    SessionQueryService sessionQueryService,
                                    AuthenticationEntryPoint unauthorizedRequestHandler) {
        this.tokenService = tokenService;
        this.sessionQueryService = sessionQueryService;
        this.unauthorizedRequestHandler = unauthorizedRequestHandler;
    }

    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, sessionQueryService);
    }

    /**
     * Prevents Spring Boot from also registering the filter as a plain servlet filter;
     * it must only run inside the security filter chain.
     */
    @Bean
    public FilterRegistrationBean<BearerAuthorizationRequestFilter> bearerFilterRegistration(
            BearerAuthorizationRequestFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(configurer -> configurer.configurationSource(_ -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }));

        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/sessions").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
