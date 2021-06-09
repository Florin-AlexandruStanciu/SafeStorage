package com.SafeStorage.service;

import com.SafeStorage.dto.AuthResponse;
import com.SafeStorage.dto.LoginCredentials;
import com.SafeStorage.model.User;
import com.SafeStorage.repositories.UserRepository;
import com.SafeStorage.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@AllArgsConstructor
public class LoginService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;


    @Transactional
    public ResponseEntity<String> createAccount(LoginCredentials loginCredentials){

        if(userRepository.findById(loginCredentials.getUsername()).isPresent()){
            return new ResponseEntity<>("Acest nume de utilizator exista deja", HttpStatus.IM_USED);
        }
        if(isEmpty(loginCredentials.getPassword())) {
            return new ResponseEntity<>("Parola nula",HttpStatus.BAD_REQUEST);
        }
        if(isEmpty(loginCredentials.getUsername())) {
            return new ResponseEntity<>("Nume de utilizator nul",HttpStatus.BAD_REQUEST);
        }

        userRepository.save( new User(
                loginCredentials.getUsername(),
                passwordEncoder.encode(loginCredentials.getPassword())
        ));

        return new ResponseEntity<>(" Utilizator creat", HttpStatus.OK);
    }

    public AuthResponse login(LoginCredentials loginCredentials) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredentials.getUsername(),
                        loginCredentials.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(loginCredentials.getUsername());
        return new AuthResponse(token,loginCredentials.getUsername());
    }
}
