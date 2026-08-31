package com.lyin.taskapi.service;

import com.lyin.taskapi.dto.AuthResponse;
import com.lyin.taskapi.dto.LoginRequest;
import com.lyin.taskapi.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
