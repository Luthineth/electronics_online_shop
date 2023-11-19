package com.store.Online.Store.repositoryTest;


import com.store.Online.Store.entity.Role;
import com.store.Online.Store.repository.roleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private roleRepository roleRepository;

    private final Role existentRole;

    private final Role nonExistentRole;

    {
        existentRole = new Role(2L,"USER");
        nonExistentRole = new Role("TEST");
    }

    @Test
    void shouldFindExistingRole() {
        Optional<Role> role = roleRepository.findRoleByRoleName(existentRole.getRoleName());
        assertThat(role.orElse(null), equalTo(existentRole));


    }

    @Test
    void shouldNotFindNonExistingRole() {
        Optional<Role> role = roleRepository.findRoleByRoleName(nonExistentRole.getRoleName());
        assertThat(Optional.empty(),equalTo(role));
    }
}
