package com.SafeStorage.repositories;

import com.SafeStorage.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
    Optional<Credentials> getCredentialsById(long id);
    List<Credentials> getByOwner(String owner);
}
