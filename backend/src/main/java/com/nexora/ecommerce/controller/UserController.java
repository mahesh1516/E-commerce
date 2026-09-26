package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.ProfileUpdateRequest;
import com.nexora.ecommerce.dto.UserResponse;
import com.nexora.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "Profile")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get my profile")
    public UserResponse getProfile() {
        return userService.getProfile();
    }

    @PutMapping
    @Operation(summary = "Update my name / phone")
    public UserResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(request);
    }
}
