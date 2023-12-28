package com.mewebstudio.javaspringbootboilerplate.config;

import com.mewebstudio.javaspringbootboilerplate.security.JwtAuthenticationEntryPoint;
import com.mewebstudio.javaspringbootboilerplate.security.JwtAuthenticationFilter;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!mvcIt")
public class WebSecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure Spring Security.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
              // Disable CSRF (Cross-Site Request Forgery) protection
              .csrf(AbstractHttpConfigurer::disable)
              // Configure exception handling to use JwtAuthenticationEntryPoint
              .exceptionHandling(configurer -> configurer
                  .authenticationEntryPoint(jwtAuthenticationEntryPoint)
              )
              // Configure session management to be stateless (no session)
              .sessionManagement(configurer -> configurer
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              )
              // Configure headers, disable frame options to enable displaying in frames
              .headers(configurer -> configurer
                  .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
              )
              // Add JwtAuthenticationFilter before the default UsernamePasswordAuthenticationFilter
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              // Authorize HTTP requests based on their paths
              .authorizeHttpRequests(requests -> requests
                  // Permit access to specific paths without authentication
                  .requestMatchers(
                      "/",
                      "/auth/**",
                      "/public/**",
                      "/assets/**",
                      "/api-docs/**",
                      "/swagger-ui/**",
                      "/webjars/**"
                  ).permitAll()
                  // Require ADMIN authority for paths starting with "/admin/"
                  .requestMatchers("/admin/**").hasAuthority(Constants.RoleEnum.ADMIN.name())
                  // Authenticate any other requests
                  .anyRequest().authenticated()
            )
            .build();
    }
}
