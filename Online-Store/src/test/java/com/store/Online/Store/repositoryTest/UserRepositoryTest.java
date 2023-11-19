package com.store.Online.Store.repositoryTest;

import com.store.Online.Store.entity.Role;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.repository.userRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private userRepository userRepository;

    private final User existentUser;

    private final User nonExistentUser;

    private final PasswordEncoder passwordEncoder;
    {

        passwordEncoder = new BCryptPasswordEncoder();
        Role adminRole = new Role(1, "ADMIN");
        existentUser = new User("Artem", "Lihachev", "sofaross228@gmail.com", "504598", adminRole);
        nonExistentUser = new User("John", "Doe", "john.doe@example.com", "password123", adminRole);

    }

    @Test
    void testExistsUser() {
        Optional<User> user = userRepository.findByEmail(existentUser.getEmail());
        assertThat(user, is(not(Optional.empty())));
        user.ifPresent(value -> {
            assertThat(value.getFirstName(), equalTo(existentUser.getFirstName()));
            assertThat(value.getSecondName(), equalTo(existentUser.getSecondName()));
            assertThat(value.getEmail(), equalTo(existentUser.getEmail()));
            assertThat(value.getRoleId(), equalTo(existentUser.getRoleId()));
            assertThat(passwordEncoder.matches(existentUser.getPassword(), value.getPassword()), is(true));
        });
    }
    @Test
    void testNotExistsUser() {
        Optional<User> user = userRepository.findByEmail(nonExistentUser.getEmail());
        assertThat(Optional.empty(),equalTo(user));
    }

    @Test
    void testNotExistsUser_SaveUser_testExistsUser() {
        Optional<User> user = userRepository.findByEmail(nonExistentUser.getEmail());
        assertThat(user.isPresent(), is(false));

        userRepository.save(nonExistentUser);

        user = userRepository.findByEmail(nonExistentUser.getEmail());
        assertThat(user.orElse(null), equalTo(nonExistentUser));
    }
}
