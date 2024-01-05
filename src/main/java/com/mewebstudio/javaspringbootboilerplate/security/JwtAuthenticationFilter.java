package com.mewebstudio.javaspringbootboilerplate.security;

import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Profile("!mvcIT")
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @Override
    protected final void doFilterInternal(@NonNull final HttpServletRequest request,
                                          @NonNull final HttpServletResponse response,
                                          @NonNull final FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.extractJwtFromRequest(request);
         // Handle CORS preflight requests
        //  if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        System.out.println("Jsut outside if");
        System.out.println("token: " + token);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {
            System.out.println("Jsut inside if");
            System.out.println("token: " + token);
            String id = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails user = userService.loadUserById(id);

            if (Objects.nonNull(user)) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationManager.authenticate(auth);
            }
        }
        // if (!request.getMethod().equals(HttpMethod.OPTIONS.name())) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        filterChain.doFilter(request, response);
        log.info(request.getRemoteAddr());
    }
}
