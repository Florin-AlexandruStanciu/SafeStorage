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
public class FileModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private byte[] name;
    @NotNull
    private byte[] type;
    @NotNull
    private byte[] bytes;

    public FileModel(byte[] name, byte[] type, byte[] bytes) {
        this.name = name;
        this.type = type;
        this.bytes = bytes;
    }
}
