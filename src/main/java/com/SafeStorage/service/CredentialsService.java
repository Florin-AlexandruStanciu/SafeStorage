package com.SafeStorage.service;


import com.SafeStorage.dto.CredentialsDto;
import com.SafeStorage.model.Credentials;
import com.SafeStorage.repositories.CredentialsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CredentialsService {

    private final CredentialsRepository credentialsRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public ResponseEntity<String> save(CredentialsDto credentialsDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        credentialsRepository.save(mapCredentials(credentialsDto));
        return new ResponseEntity<>("Credentiale salvate", HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<CredentialsDto> getAll(){
        return credentialsRepository.getByOwner(getConnectedUsername()).stream()
                .map(cred -> {
                    try {
                        return new CredentialsDto(
                                cred.getSite(),
                                encryptionService.decryptData(cred.getPassword(), cred.getUsername()),
                                cred.getPassword());
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    return new CredentialsDto();
                })
                .collect(Collectors.toList());
    }


    private Credentials mapCredentials(CredentialsDto credentialsDto) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        return new Credentials(
                getConnectedUsername(),
                credentialsDto.getSite(),
                encryptionService.encrypt(credentialsDto.getPassword(), credentialsDto.getUsername()),
                credentialsDto.getPassword());
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
