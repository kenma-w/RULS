package com.chat.ruls.service;

import com.chat.ruls.model.User;
import java.util.Map;

public interface AuthService {
    User loginUser(Map<String, String> paramMap);
    User signUp(User user);
    String forgotPassword1(String email);
    String forgotPassword2(String email, String otp);
    User forgotPassword3(String email, String password);
}

