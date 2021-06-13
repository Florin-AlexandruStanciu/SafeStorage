package com.SafeStorage.repositories;

import com.SafeStorage.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileModel,Long> {
    Optional<FileModel> getById(Long id);
}
