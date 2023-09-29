package com.store.Online.Store.repository;

import com.store.Online.Store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);
}

