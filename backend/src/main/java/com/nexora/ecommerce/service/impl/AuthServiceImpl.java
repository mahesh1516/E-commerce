package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.LoginRequest;
import com.nexora.ecommerce.dto.LoginResponse;
import com.nexora.ecommerce.dto.UserRegisterRequest;
import com.nexora.ecommerce.entity.Role;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.exception.UnauthorizedException;
import com.nexora.ecommerce.mapper.UserMapper;
import com.nexora.ecommerce.repository.RoleRepository;
import com.nexora.ecommerce.repository.UserRepository;
import com.nexora.ecommerce.security.JwtService;
import com.nexora.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor // Lombok: constructor for all final fields
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponse register(UserRegisterRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Role userRole = roleRepository.findByName(Role.USER)
                .orElseGet(() -> roleRepository.save(new Role(Role.USER)));

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password())); // hash, never plain text
        user.setPhone(request.phone());
        user.getRoles().add(userRole);

        userRepository.save(user);
        log.info("New user registered: id={}", user.getId()); // no email/password in logs

        return buildResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalize(request.email());

        // Spring Security loads the user and checks the BCrypt hash.
        // Wrong password -> BadCredentialsException -> 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        log.info("User logged in: id={}", user.getId());
        return buildResponse(user);
    }

    private LoginResponse buildResponse(User user) {
        String role = UserMapper.roleName(user);
        String token = jwtService.generateToken(user.getEmail(), role);
        return new LoginResponse(token, "Bearer", user.getId(), user.getName(),
                user.getEmail(), role, jwtService.getExpirationMs());
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
