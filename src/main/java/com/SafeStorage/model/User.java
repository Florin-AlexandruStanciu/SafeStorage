package com.SafeStorage.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@NoArgsConstructor
@Entity
public class User {
    @Id
    @NotNull
    private String username;
    @NotNull
    private String password;

    public User (String username, String password){
        this.username = username;
        this.password = password;
    }

}