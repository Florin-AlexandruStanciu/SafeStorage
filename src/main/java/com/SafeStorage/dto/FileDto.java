package com.SafeStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private String name;
    private String type;
    private byte[] bytes;
}
