package com.medical.management.auth.service;

import com.medical.management.auth.dto.AuthResponse;
import com.medical.management.auth.dto.LoginRequest;
import com.medical.management.auth.dto.RefreshTokenRequest;
import com.medical.management.auth.dto.RegisterRequest;
import com.medical.management.users.dto.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    UserResponse getCurrentUser();
}
