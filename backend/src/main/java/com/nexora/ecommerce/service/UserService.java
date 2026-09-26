package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.ProfileUpdateRequest;
import com.nexora.ecommerce.dto.UserResponse;

public interface UserService {

    UserResponse getProfile();

    UserResponse updateProfile(ProfileUpdateRequest request);
}
