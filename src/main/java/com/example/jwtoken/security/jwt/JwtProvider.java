package com.example.jwtoken.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.jwtoken.security.UserDetailsServiceImpl;
import com.example.jwtoken.service.UserService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Log4j2
public class JwtProvider {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtProvider(UserService userService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expired}")
    private Long jwtLifetime;

    @PostConstruct
    public void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String getToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);
        Optional<User> userOptional = userService.findByUsername(username);
        userOptional.ifPresent(user -> claims.put("roles", getRoleNames(user.getRoles())));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtLifetime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer_"))
            return bearerToken.substring(7);

        return null;
    }

    public boolean validate(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("JWT is expired or invalid!");
        }
    }

    private List<String> getRoleNames(List<Role> roles) {
         List<String> result = new ArrayList<>();

         for (Role userRole : roles)
             result.add(userRole.getName());

         return result;
    }
}
