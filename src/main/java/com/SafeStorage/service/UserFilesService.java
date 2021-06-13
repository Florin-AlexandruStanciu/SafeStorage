package com.SafeStorage.service;


import com.SafeStorage.dto.FileDto;
import com.SafeStorage.dto.SaveFileDto;
import com.SafeStorage.dto.UserFileDto;
import com.SafeStorage.model.FileModel;
import com.SafeStorage.model.UserFile;
import com.SafeStorage.repositories.FileRepository;
import com.SafeStorage.repositories.UserFilesRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.SafeStorage.service.LoginService.getConnectedUsername;

@AllArgsConstructor
@Service
public class UserFilesService {

    private final UserFilesRepository userFilesRepository;
    private final FileRepository fileRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public ResponseEntity<String> save(SaveFileDto saveFileDto) throws Exception {
        userFilesRepository.save(mapUserFile(saveFileDto));
        return new ResponseEntity<>("Fisier salvat", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<UserFileDto> getAllFileNames(String password){
        return userFilesRepository.getByOwner(getConnectedUsername()).stream()
                .map(file-> {
                    try {
                        return new UserFileDto(
                                file.getId(),
                                new String(encryptionService.decrypt(password,file.getName()))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new UserFileDto(); })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FileDto getWholeFile(Long id, String password){
        checkFileOwner(id);
        return fileRepository.getById(id)
                .map(f-> {
                    try {
                        return mapDto(f,password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new FileDto();
                }).orElse(new FileDto());
    }

    @Transactional
    public ResponseEntity<String> delete(Long id){
        try {
            checkFileOwner(id);
            userFilesRepository.deleteById(id);
            fileRepository.deleteById(id);
        } catch (IllegalStateException e){
           return  new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Fisier sters",HttpStatus.OK);
    }

    private FileDto mapDto(FileModel fileModel, String password) throws Exception {
        return new FileDto(
                new String(encryptionService.decrypt(password,fileModel.getName())),
                new String(encryptionService.decrypt(password,fileModel.getType())),
                encryptionService.decrypt(password,fileModel.getBytes())
        );
    }

    private FileModel mapFile(SaveFileDto fileDto) throws Exception {
        return new FileModel(
                encryptionService.encrypt(fileDto.getPassword(),fileDto.getFileDto().getName().getBytes()),
                encryptionService.encrypt(fileDto.getPassword(),fileDto.getFileDto().getType().getBytes()),
                encryptionService.encrypt(fileDto.getPassword(),fileDto.getFileDto().getBytes())
        );
    }


    private UserFile mapUserFile(SaveFileDto saveFileDto) throws Exception {
        return new UserFile(
                fileRepository.save(mapFile(saveFileDto)).getId(),
                getConnectedUsername(),
                encryptionService.encrypt(saveFileDto.getPassword(),saveFileDto.getFileDto().getName().getBytes())
        );
    }

    private void checkFileOwner(Long id){
        Optional<UserFile> optional = userFilesRepository.getById(id);
        if(optional.isPresent()){
            if(!getConnectedUsername().equals(optional.get().getOwner())){
                throw new IllegalStateException("Fisierul nu iti apartine");
            }
        } else {
            throw new IllegalStateException("Fisierul nu exista");
        }
    }

//    private byte[] encryptFileName(String name, String password) throws Exception {
//        String [] splits = name.split("\\.");
//        return encryptionService.encrypt(password,splits[splits.length-1].getBytes());
//    }
}