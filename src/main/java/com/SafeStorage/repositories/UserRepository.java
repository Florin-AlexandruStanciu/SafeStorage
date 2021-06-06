package com.SafeStorage.repositories;

import com.SafeStorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
