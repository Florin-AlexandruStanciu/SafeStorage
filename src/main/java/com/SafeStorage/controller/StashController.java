package com.SafeStorage.controller;

import com.SafeStorage.dto.CredentialsDto;
import com.SafeStorage.dto.CredentialsSaveDto;
import com.SafeStorage.service.CredentialsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/safe-storage/stash")
@AllArgsConstructor
public class StashController {

    private final CredentialsService credentialsService;

    @PostMapping("/create")
    public ResponseEntity<String> createCredentials(@RequestBody CredentialsSaveDto credentialsSaveDto) throws Exception {
        return credentialsService.save(credentialsSaveDto);
    }

    @PostMapping
    public List<CredentialsDto> getCredentials(@RequestBody String password) {
        return credentialsService.getAll(password);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCredentials(@RequestBody Long id) {
        return credentialsService.delete(id);
    }

}
