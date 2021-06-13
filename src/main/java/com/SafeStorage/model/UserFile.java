package com.SafeStorage.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserFile {
    @Id
    private Long id;
    @NotNull
    private String owner;
    @NotNull
    private byte [] name;
}
