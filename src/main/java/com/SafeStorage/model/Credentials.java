package com.SafeStorage.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String owner;
    private String site;
    private byte[] username;
    private String password;

    public Credentials(String owner, String site, byte[] username, String password) {
        this.owner = owner;
        this.site = site;
        this.username = username;
        this.password = password;
    }
}
