package com.SafeStorage.repositories;

import com.SafeStorage.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFilesRepository extends JpaRepository<UserFile,Long> {
    List<UserFile> getByOwner(String owner);
    Optional<UserFile> getById(Long id);
}