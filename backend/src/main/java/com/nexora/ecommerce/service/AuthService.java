package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.LoginRequest;
import com.nexora.ecommerce.dto.LoginResponse;
import com.nexora.ecommerce.dto.UserRegisterRequest;

public interface AuthService {

    LoginResponse register(UserRegisterRequest request);

    LoginResponse login(LoginRequest request);
}
