package com.store.Online.Store.repository;

import com.store.Online.Store.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface roleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findRoleByRoleName(String roleName);
}
