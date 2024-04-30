package com.luv2code.ecommerce.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.ecommerce.controller.ApiError;
import com.luv2code.ecommerce.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    private static final Logger logger = Logger.getLogger(JwtRequestFilter.class);

    private final UserService userService;

    private final List<String> publicUrls = Arrays.asList("/api/user/login", "/api/admins/login", "/api/user/register", "/swagger-ui",
            "/api/users/forget-password", "/api/users/forget_password_code");

    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        final String authorizationHeader = request.getHeader("Authorization");
//        String username = null;
//        String jwt = null;
//
//        String reqPath = request.getRequestURI();
//
//        // If not matched then continue to the next filter
//        if ("/api/user/register".equals(reqPath ) || "/api/user/login".equals(reqPath )) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        if(authorizationHeader == null){
//            handleException(response, HttpStatus.UNAUTHORIZED, "Authentication token is needed");
//            return;
//        }
//
//
//        if (authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            try {
//                username = jwtUtil.extractUsername(jwt);
//            } catch (ExpiredJwtException e) {
//                handleException(response, HttpStatus.UNAUTHORIZED, "JWT Expired");
//                return;
//            } catch (MalformedJwtException | SignatureException e) {
//                handleException(response, HttpStatus.UNAUTHORIZED, "Invalid Token");
//                return;
//            }
//        }
//        if(SecurityContextHolder.getContext().getAuthentication() != null){
//            String name = SecurityContextHolder.getContext().getAuthentication().getName();
//            int x = 0;
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.loadUserByUsername(username);
//            if (jwtUtil.validateToken(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        chain.doFilter(request, response);

        // Get authorization header and validate
        final String header = request.getHeader("Authorization");
        if (header==null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
            chain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context
        UserDetails userDetails = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        if(!userDetails.isEnabled()){
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ?
                        List.of() : userDetails.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ApiError error = new ApiError(status, message);
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}