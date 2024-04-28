package com.luv2code.ecommerce.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${application.secretKey}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, boolean mobile) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        return createToken(claims, userDetails.getUsername(), mobile);
    }

    public String generateUnlimitedToken(UserDetails userDetails, boolean mobile){
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        return createUnlimitedToken(claims, userDetails.getUsername(), mobile);
    }



    private String createToken(Map<String, Object> claims, String subject, boolean mobile) {
        if (subject.equals("nima2142.p30+test@gmail.com") || subject.equals("nima2142.p30@gmail.com")) {
            return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + Integer.MAX_VALUE))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
        }
        Date expiration;
        if (mobile) {
            expiration = new Date(Long.MAX_VALUE);
        } else {
            expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        }
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private String createUnlimitedToken(Map<String, Object> claims, String subject, boolean mobile) {
        Date expiration;
        if (mobile) {
            expiration = new Date(Long.MAX_VALUE);
        } else {
            expiration = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
//          Long.MAX_VALUE(9,223,372,036,854,775,807) > 1000L * 60 * 60 * 24 * 365(31,536,000,000)
        }
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
