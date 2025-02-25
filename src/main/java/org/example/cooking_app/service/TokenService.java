package org.example.cooking_app.service;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.cooking_app.entity.Role;
import org.example.cooking_app.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class TokenService {

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        StringJoiner joiner = new StringJoiner(",");
        for (Role role : user.getRoles()) {
            joiner.add(role.getRoleName().toString());
        }
        claims.put("roles",joiner.toString());

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .signWith(signKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .compact();
    }



    private SecretKey signKey() {
        return Keys.hmacShaKeyFor("12345678901234567890123456789012".getBytes());
    }



    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private Claims getClaims(String token) {
        return Jwts.parser().
                verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String getUserName(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }


    public List<SimpleGrantedAuthority> getRoles(String token) {
        Claims claims = getClaims(token);
        String roles = (String) claims.get("roles");
        return Arrays.stream(roles.split(",")).map(SimpleGrantedAuthority::new).toList();
    }

}
