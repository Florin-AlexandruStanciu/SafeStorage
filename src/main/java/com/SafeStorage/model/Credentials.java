package com.SafeStorage.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String owner;
    private byte[] site;
    private byte[] username;
    private byte[] password;

    public Credentials(String owner, byte[] site, byte[] username, byte[] password) {
        this.owner = owner;
        this.site = site;
        this.username = username;
        this.password = password;
    }
}
