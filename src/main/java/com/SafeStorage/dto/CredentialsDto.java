package com.SafeStorage.dto;


import com.SafeStorage.model.Credentials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsDto {
    private String site;
    private byte[] username;
    private String password;

    public CredentialsDto(Credentials credentials) {
        this.site = credentials.getSite();
        this.username = credentials.getUsername();
        this.password = credentials.getPassword();
    }
}
