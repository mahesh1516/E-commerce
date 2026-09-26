package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.UserResponse;
import com.nexora.ecommerce.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                roleName(u), u.getCreatedAt());
    }

    /** "ADMIN" or "USER" - the simple role name the frontend uses. */
    public static String roleName(User u) {
        return u.isAdmin() ? "ADMIN" : "USER";
    }
}
