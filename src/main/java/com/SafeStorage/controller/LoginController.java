package com.SafeStorage.controller;

import com.SafeStorage.dto.AuthResponse;
import com.SafeStorage.dto.LoginCredentials;
import com.SafeStorage.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/safe-storage/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/create-account")
    public ResponseEntity<String> create(@RequestBody LoginCredentials loginCredentials){
        return loginService.createAccount(loginCredentials);
    }

    @PostMapping("/authenticate")
    public AuthResponse login(@RequestBody LoginCredentials loginCredentials){
        return loginService.login(loginCredentials);
    }

}
