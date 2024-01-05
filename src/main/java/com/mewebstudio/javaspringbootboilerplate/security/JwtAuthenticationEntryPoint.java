package com.mewebstudio.javaspringbootboilerplate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.exception.AppExceptionHandler;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final MessageSourceService messageSourceService;

    private final ObjectMapper objectMapper;

    /**
     * Commences the authentication entry point for JWT authentication failures.
     * Handles and logs the specific type of JWT authentication exception.
     * Writes an appropriate JSON response to the client indicating the authentication failure.
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param e           AuthenticationException representing the authentication failure
     * @throws IOException if an input or output exception occurs
     */

    @Override
    public final void commence(final HttpServletRequest request,
                               final HttpServletResponse response,
                               final AuthenticationException e) throws IOException {
        // Extract specific error messages from request attributes
        final String expired = (String) request.getAttribute("expired");
        final String unsupported = (String) request.getAttribute("unsupported");
        final String invalid = (String) request.getAttribute("invalid");
        final String illegal = (String) request.getAttribute("illegal");
        final String notfound = (String) request.getAttribute("notfound");
        final String message;

        // Determine the appropriate error message
        if (expired != null) {
            message = expired;
        } else if (unsupported != null) {
            message = unsupported;
        } else if (invalid != null) {
            message = invalid;
        } else if (illegal != null) {
            message = illegal;
        } else if (notfound != null) {
            message = notfound;
        } else {
            message = e.getMessage();
        }

        // Log the error
        log.error("Could not set user authentication in security context. Error: {}", message);

        // Handle the exception and construct a ResponseEntity for the response
        ResponseEntity<ErrorResponse> responseEntity = new AppExceptionHandler(messageSourceService)
            .handleBadCredentialsException(new BadCredentialsException(message));


        System.out.println("in Jwt Authentication Entry Point --- " + message);

        // Write the JSON response to the client
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
