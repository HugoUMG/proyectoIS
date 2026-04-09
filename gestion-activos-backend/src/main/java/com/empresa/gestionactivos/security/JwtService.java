package com.empresa.gestionactivos.security;

import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public String extractUsername(String token) {
        return token;
    }

    public boolean isTokenValid(String token, String username) {
        return token != null && username != null;
    }
}
