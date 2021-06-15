package com.SafeStorage.controller;

import com.SafeStorage.dto.*;
import com.SafeStorage.service.CredentialsService;
import com.SafeStorage.service.UserFilesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/safe-storage/stash")
@AllArgsConstructor
public class StashController {

    private final CredentialsService credentialsService;
    private final UserFilesService userFilesService;

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

    @PostMapping("/files/create")
    public ResponseEntity<String> saveFile(@RequestBody SaveFileDto saveFileDto) throws Exception {
        return userFilesService.save(saveFileDto);
    }

    @PostMapping("/files/getAll")
    public List<UserFileDto> getAllFiles(@RequestBody String password) {
        return userFilesService.getAllFileNames(password);
    }

    @PostMapping("files/getWholeFile")
    public FileDto getFile(@RequestBody GetFileDto getFileDto){
        return userFilesService.getWholeFile(getFileDto.getId(),getFileDto.getPassword());
    }

    @PostMapping("/files/delete")
    public ResponseEntity<String> deleteFile(@RequestBody Long id){
       return userFilesService.delete(id);
    }

}
