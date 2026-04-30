package com.scrumcat.service;

import com.scrumcat.dto.LoginRequest;
import com.scrumcat.dto.RegisterRequest;
import com.scrumcat.vo.LoginResponse;
import com.scrumcat.vo.UserInfoVO;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserInfoVO getCurrentUser();
}
