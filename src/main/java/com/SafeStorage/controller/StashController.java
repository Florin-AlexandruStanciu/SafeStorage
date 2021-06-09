package com.SafeStorage.controller;

import com.SafeStorage.dto.CredentialsDto;

import com.SafeStorage.dto.CredentialsSaveDto;
import com.SafeStorage.service.CredentialsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/safe-storage/stash")
@AllArgsConstructor
public class StashController {

    private final CredentialsService credentialsService;

    @PostMapping
    public ResponseEntity<String> createCredentials(@RequestBody CredentialsSaveDto credentialsSaveDto) throws Exception {
        return credentialsService.save(credentialsSaveDto);
    }

    @GetMapping
    public List<CredentialsDto> getCredentials(@RequestBody String password) {
        return credentialsService.getAll(password);
    }
}
