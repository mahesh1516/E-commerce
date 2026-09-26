package com.nexora.ecommerce.util;

import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.UnauthorizedException;
import com.nexora.ecommerce.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Gives services access to the currently logged-in user. */
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("You must be logged in");
        }
        // The JWT subject (username) is the user's email
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User account not found"));
    }
}
