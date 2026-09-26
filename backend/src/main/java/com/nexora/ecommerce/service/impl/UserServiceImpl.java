package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.ProfileUpdateRequest;
import com.nexora.ecommerce.dto.UserResponse;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.mapper.UserMapper;
import com.nexora.ecommerce.service.UserService;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile() {
        return UserMapper.toResponse(securityUtils.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(ProfileUpdateRequest request) {
        User user = securityUtils.getCurrentUser();
        user.setName(request.name().trim());
        user.setPhone(request.phone());
        // No save() needed: the entity is "managed", changes are
        // written automatically when the transaction commits.
        return UserMapper.toResponse(user);
    }
}
