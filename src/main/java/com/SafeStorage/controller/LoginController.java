package com.SafeStorage.controller;

import com.SafeStorage.dto.AuthResponse;
import com.SafeStorage.dto.ChangePasswordDto;
import com.SafeStorage.dto.LoginCredentials;
import com.SafeStorage.service.CredentialsService;
import com.SafeStorage.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/safe-storage")
public class LoginController {

    private final LoginService loginService;
    private final CredentialsService credentialsService;

    public LoginController(LoginService loginService, CredentialsService credentialsService) {
        this.loginService = loginService;
        this.credentialsService = credentialsService;
    }

    @PostMapping("/auth/create-account")
    public ResponseEntity<String> create(@RequestBody LoginCredentials loginCredentials){
        return loginService.createAccount(loginCredentials);
    }

    @PostMapping("/auth/authenticate")
    public AuthResponse login(@RequestBody LoginCredentials loginCredentials){
        return loginService.login(loginCredentials);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) throws Exception {
        credentialsService.changePassword(changePasswordDto);
        return loginService.changePassword(changePasswordDto.getNewPassword());
    }


}
