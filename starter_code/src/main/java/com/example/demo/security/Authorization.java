package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Authorization {
    public Authorization() {
    }

    public static boolean checkPersonalAccess(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getPrincipal().toString();
        return username.equals(currentUsername);
    }
}
