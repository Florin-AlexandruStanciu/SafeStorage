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
    public ResponseEntity<String> save(CredentialsSaveDto saveDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        credentialsRepository.save(mapCredentials(saveDto));
        return new ResponseEntity<>("Credentiale salvate", HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<CredentialsDto> getAll(String masterPassword){

        return credentialsRepository.getByOwner(getConnectedUsername()).stream()
                .map(cred-> {
                    try {
                        return mapDto(cred,masterPassword);
                    } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    return new CredentialsDto();
                })
                .collect(Collectors.toList());
    }

    private CredentialsDto mapDto(Credentials credentials, String masterPassword) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        return new CredentialsDto(
                new String(encryptionService.decryptData(masterPassword, credentials.getSite())),
                new String(encryptionService.decryptData(masterPassword, credentials.getUsername())),
                new String(encryptionService.decryptData(masterPassword, credentials.getPassword())));
    }


    private Credentials mapCredentials(CredentialsSaveDto saveDto) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        return new Credentials(
                getConnectedUsername(),
                encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getSite().getBytes()),
                encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getUsername().getBytes()),
                encryptionService.encrypt(saveDto.getMasterPassword(), saveDto.getCredentials().getPassword().getBytes()));
    }

    private byte[] getConnectedUsername() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username.getBytes();
    }
}
