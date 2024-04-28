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

    private final JwtUtil jwtUtil;

    private static final Logger logger = Logger.getLogger(JwtRequestFilter.class);

    private final UserService userService;

    private final List<String> publicUrls = Arrays.asList("/api/users/login", "/api/admins/login", "/api/users/register", "/swagger-ui",
            "/api/users/forget-password", "/api/users/forget_password_code");

    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {



//        logger.debug("request reached.....");

        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String requestURI = httpServletRequest.getRequestURI();
        //logger.debug("url: " + httpServletRequest.getRequestURL());
        requestURI = requestURI.replaceAll("\\d", "");
        if (requestURI.charAt(requestURI.length() - 1) == '/') {
            requestURI = requestURI.substring(0, requestURI.length() - 1);
        }

        if ((authorizationHeader != null && !authorizationHeader.equals("")) || ((!publicUrls.contains(requestURI) ))) {

            String username = null;
            String token;

            try {
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);

                    username = jwtUtil.extractUsername(token);
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (userDetails == null) {
                        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                        httpServletResponse.setContentType("application/json");
                        httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(HttpStatus.UNAUTHORIZED, "Invalid Token", "Invalid User")));
                        return;
                    }
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

            } catch (ExpiredJwtException e) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(
                        HttpStatus.UNAUTHORIZED, "JWT Expired", "JWT Expired")));
                return;
            } catch (MalformedJwtException e) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(
                        HttpStatus.UNAUTHORIZED, "Invalid Token", "MalformedJwtException occurred")));
                return;
            } catch (SignatureException e) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(
                        HttpStatus.UNAUTHORIZED, "Invalid Token", "SignatureException occurred")));
                return;
            } catch (AuthenticationException ex) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(
                        HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getMessage())));
                return;
            }
//            logger.debug("do filter in request filter.......");
        }
        //TODO:REZA All request except Get Must Have token
//        if ((authorizationHeader == null || authorizationHeader.equals("")) && !httpServletRequest.getMethod().equals(HttpMethod.GET.toString())) {
//            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
//            httpServletResponse.setContentType("application/json");
//            httpServletResponse.getWriter().write(convertObjectToJson(new ApiError(
//                    HttpStatus.UNAUTHORIZED, "NO Token", "Token Must be send")));
//            return;
//        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}