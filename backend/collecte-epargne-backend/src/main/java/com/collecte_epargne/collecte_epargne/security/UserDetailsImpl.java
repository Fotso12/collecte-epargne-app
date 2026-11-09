package com.collecte_epargne.collecte_epargne.security;

import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Utilisateur utilisateur;

    public UserDetailsImpl(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleCode = utilisateur.getRole().getCode().toUpperCase();
        System.out.println("USER_LOGIN_DEBUG: User " + utilisateur.getEmail() + " has Role Code: " + roleCode + " -> Authority: ROLE_" + roleCode);
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + roleCode)
        );
    }

    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail(); // login par email
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
