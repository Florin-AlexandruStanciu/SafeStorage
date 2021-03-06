package com.SafeStorage.service;


import com.SafeStorage.dto.ChangePasswordDto;
import com.SafeStorage.dto.CredentialsDto;
import com.SafeStorage.dto.CredentialsSaveDto;
import com.SafeStorage.model.Credentials;
import com.SafeStorage.repositories.CredentialsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.SafeStorage.service.LoginService.getConnectedUsername;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@AllArgsConstructor
public class CredentialsService {

    private final CredentialsRepository credentialsRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public ResponseEntity<String> save(CredentialsSaveDto saveDto) throws Exception {
        try {
            credentialsRepository.save(mapCredentials(saveDto));
        } catch (IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Credentiale salvate", HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<CredentialsDto> getAll(String password){

        return credentialsRepository.getByOwner(getConnectedUsername()).stream()
                .map(cred-> {
                    try {
                        return mapDto(cred,password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new CredentialsDto();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<String> delete(Long id){
        try {
            checkCredentialOwner(id);
            credentialsRepository.deleteById(id);
        } catch (IllegalStateException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Credentiale sterse", HttpStatus.OK);
    }

    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto) throws Exception {
        if(isEmpty(changePasswordDto.getNewPassword())){
            throw new IllegalArgumentException("Parola nula");
        }
        for (Credentials credentials : credentialsRepository.getByOwner(getConnectedUsername())) {
            Credentials changePasswordForOneEntry = changePasswordForOneEntry(credentials,changePasswordDto);
            credentialsRepository.save(changePasswordForOneEntry);
        }
    }

    private Credentials changePasswordForOneEntry(Credentials creds, ChangePasswordDto changePasswordDto) throws Exception {
        creds.setPassword(encryptionService.changePassword(creds.getPassword(),changePasswordDto));
        creds.setUsername(encryptionService.changePassword(creds.getUsername(),changePasswordDto));
        creds.setSite(encryptionService.changePassword(creds.getSite(),changePasswordDto));
        return creds;
    }

    private CredentialsDto mapDto(Credentials credentials, String masterPassword) throws Exception {
        return new CredentialsDto(
                credentials.getId(),
                new String(encryptionService.decrypt(masterPassword, credentials.getSite())),
                new String(encryptionService.decrypt(masterPassword, credentials.getUsername())),
                new String(encryptionService.decrypt(masterPassword, credentials.getPassword())));
    }


    private Credentials mapCredentials(CredentialsSaveDto saveDto) throws Exception {
        if(saveDto.getCredentials().getId() < 1) {
            return new Credentials(
                    getConnectedUsername(),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getSite().getBytes()),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getUsername().getBytes()),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getPassword().getBytes()));
        } else {
            checkCredentialOwner(saveDto.getCredentials().getId());
            return new Credentials(
                    saveDto.getCredentials().getId(),
                    getConnectedUsername(),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getSite().getBytes()),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getUsername().getBytes()),
                    encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getPassword().getBytes()));
        }
    }

    private void checkCredentialOwner(Long id){
        Optional<Credentials> optional = credentialsRepository.getById(id);
        if(optional.isPresent()){
            if(!getConnectedUsername().equals(optional.get().getOwner())){
                throw new IllegalStateException("Credentialele nu iti apartin");
            }
        } else {
            throw new IllegalStateException("Credentialele au fost deja sterse");
        }
    }

}
