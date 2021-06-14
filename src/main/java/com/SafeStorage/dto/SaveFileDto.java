package com.SafeStorage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveFileDto {
    private String password;
    private FileDto fileDto;

}
