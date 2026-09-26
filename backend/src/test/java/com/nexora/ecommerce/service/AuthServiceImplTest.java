package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.LoginRequest;
import com.nexora.ecommerce.dto.LoginResponse;
import com.nexora.ecommerce.dto.UserRegisterRequest;
import com.nexora.ecommerce.entity.Role;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.repository.RoleRepository;
import com.nexora.ecommerce.repository.UserRepository;
import com.nexora.ecommerce.security.JwtService;
import com.nexora.ecommerce.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthServiceImpl authService;

    @Test
    void register_createsUserWithHashedPassword_andReturnsToken() {
        when(userRepository.existsByEmail("mahesh@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.of(new Role(Role.USER)));
        when(passwordEncoder.encode("Secret@123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken("mahesh@example.com", "USER")).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        LoginResponse response = authService.register(
                new UserRegisterRequest("Mahesh", "  Mahesh@Example.com ", "Secret@123", null));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.userId()).isEqualTo(1L);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed-password"); // never plain text
        assertThat(saved.getValue().getEmail()).isEqualTo("mahesh@example.com");
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail("user@nexora.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(
                new UserRegisterRequest("Mahesh", "user@nexora.com", "Secret@123", null)));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("user@nexora.com", "wrong")));

        verifyNoInteractions(jwtService);
    }
}
