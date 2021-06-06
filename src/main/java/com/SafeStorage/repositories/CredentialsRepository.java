package com.SafeStorage.repositories;

import com.SafeStorage.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
    List<Credentials> getByOwner(byte[] owner);
}
