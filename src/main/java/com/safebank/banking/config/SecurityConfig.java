package com.safebank.banking.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.safebank.banking.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // CSRF
            .csrf(csrf -> csrf.disable())

            // CORS
            .cors(Customizer.withDefaults())

            // JWT = stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // AUTHORIZATION
            .authorizeHttpRequests(auth -> auth

                // CORS
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // PUBLIC CREATE ACCOUNT
                .requestMatchers(
                    HttpMethod.POST,
                    "/users/**"
                ).permitAll()

                // ADMIN LOGIN
                .requestMatchers(
                    HttpMethod.POST,
                    "/admin/login"
                ).permitAll()

                // AUTH
                .requestMatchers(
                    "/auth/**"
                ).permitAll()

                // GOOGLE OAUTH
                .requestMatchers(
                    "/oauth2/**",
                    "/login/**"
                ).permitAll()

                // ADMIN APIs
                .requestMatchers(
                    "/admin/**"
                ).hasRole("ADMIN")
                
                // TRANSACTIONS
                .requestMatchers(
                    "/transactions/**"
                ).hasRole("USER")

                // USER APIs
                .requestMatchers(
                    "/users/**"
                ).hasAnyRole("USER", "ADMIN")

                // OTHER APIs
                .anyRequest().authenticated()
            )

            // Disable form login
            .formLogin(
                form -> form.disable()
            )

            // Disable basic authentication
            .httpBasic(
                basic -> basic.disable()
            )

            // JWT filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            Arrays.asList(
                "http://localhost:5173"
            )
        );

        configuration.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            Arrays.asList(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}