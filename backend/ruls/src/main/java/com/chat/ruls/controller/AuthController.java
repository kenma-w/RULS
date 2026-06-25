package com.chat.ruls.controller;

import com.chat.ruls.model.User;
import com.chat.ruls.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> userLogin(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> paramMap) {
        LOG.info("User login attempt: {}", paramMap.get("email"));
        User res = authService.loginUser(paramMap);
        if (res != null) {
            LOG.info("User login successful: {}", res.getEmail());
            return ResponseEntity.ok(res);
        } else {
            LOG.info("User login failed: {}", paramMap.get("email"));
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> userRegister(@RequestHeader HttpHeaders headers, @RequestBody User newUser) {
        LOG.info("Registering user: {}", newUser.getEmail());
        User res = authService.signUp(newUser);
        if (res != null) {
            LOG.info("User registered successfully: {}", res.getEmail());
            return ResponseEntity.ok(res);
        } else {
            LOG.info("User registration failed: {}", newUser.getEmail());
            return ResponseEntity.status(409).body(null);
        }
    }

    @CrossOrigin
    @PostMapping("/forgotPassword1")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> paramMap) {
        String response = authService.forgotPassword1(paramMap.get("email"));
        return ResponseEntity.ok(response);
    }

    @CrossOrigin
    @PostMapping("/forgotPassword2")
    public ResponseEntity<String> forgotPassword2(@RequestBody Map<String, String> paramMap) {
        String response = authService.forgotPassword2(paramMap.get("email"), paramMap.get("otp"));
        return ResponseEntity.ok(response);
    }

    @CrossOrigin
    @PostMapping("/forgotPassword3")
    public ResponseEntity<User> forgotPassword3(@RequestBody Map<String, String> paramMap) {
        User res = authService.forgotPassword3(paramMap.get("email"), paramMap.get("password"));
        return ResponseEntity.ok(res);
    }
}
