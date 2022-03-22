package com.example.jwtoken.security.jwt;

import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    public JwtUserFactory() {

    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                Objects.equals(user.getStatus(), Status.ACTIVE),
                user.getUpdated(),
                getGrantedAuthorities(user.getRoles())
        );
    }

    private static List<GrantedAuthority> getGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream().
                map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
