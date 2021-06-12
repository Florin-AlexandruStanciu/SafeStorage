package com.SafeStorage.service;


import com.SafeStorage.dto.CredentialsDto;
import com.SafeStorage.dto.CredentialsSaveDto;
import com.SafeStorage.model.Credentials;
import com.SafeStorage.repositories.CredentialsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CredentialsService {

    private final CredentialsRepository credentialsRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public ResponseEntity<String> save(CredentialsSaveDto saveDto) throws Exception {
        try {
            credentialsRepository.save(mapCredentials(saveDto));
        } catch (Exception e){
            if ( "Credentialele nu iti apartin".equals(e.getMessage())){
                return new ResponseEntity<>("Credentialele nu iti apartin", HttpStatus.BAD_REQUEST);
            } else {
                throw e;
            }
        }
        return new ResponseEntity<>("Credentiale salvate", HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<CredentialsDto> getAll(String masterPassword){

        return credentialsRepository.getByOwner(getConnectedUsername()).stream()
                .map(cred-> {
                    try {
                        return mapDto(cred,masterPassword);
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
        } catch (Exception e){
            if ( "Credentialele nu iti apartin".equals(e.getMessage())){
                return new ResponseEntity<>("Credentialele nu iti apartin", HttpStatus.BAD_REQUEST);
            } else {
                throw e;
            }
        }
        return new ResponseEntity<>("Credentiale sterse", HttpStatus.OK);
    }

    private CredentialsDto mapDto(Credentials credentials, String masterPassword) throws Exception {
        return new CredentialsDto(
                credentials.getId(),
                new String(encryptionService.decryptData(masterPassword, credentials.getSite())),
                new String(encryptionService.decryptData(masterPassword, credentials.getUsername())),
                new String(encryptionService.decryptData(masterPassword, credentials.getPassword())));
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

    private void checkCredentialOwner(long id){
        Optional<Credentials> optional = credentialsRepository.getCredentialsById(id);
        if(optional.isPresent()){
            if(!getConnectedUsername().equals(optional.get().getOwner())){
                throw new IllegalStateException("Credentialele nu iti apartin");
            }
        }
    }

    private String getConnectedUsername() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }
}
