package com.SafeStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsSaveDto {
    private CredentialsDto credentials;
    private String masterPassword;
}
