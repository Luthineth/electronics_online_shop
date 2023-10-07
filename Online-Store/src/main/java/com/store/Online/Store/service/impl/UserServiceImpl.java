package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.AuthenticationRequest;
import com.store.Online.Store.dto.UserRequest;
import com.store.Online.Store.entity.Role;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.config.jwt.JwtTokenUtil;
import com.store.Online.Store.exception.RoleDefinitionException;
import com.store.Online.Store.exception.UserCreationException;
import com.store.Online.Store.repository.roleRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements userService {

    private final userRepository userRepository;

    private final roleRepository roleRepository;

    public final JwtTokenUtil jwtTokenUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(userRepository userRepository, roleRepository roleRepository, JwtTokenUtil jwtTokenUtil,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.roleRepository  = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil    = jwtTokenUtil;
    }

    @Override
    public void register(UserRequest userRequest){

        User user = new User();
        if (!isValidEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Incorrect email format");
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserCreationException("The user with this email" + user.getEmail() + "already exists");
        }
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setSecondName(userRequest.getSecondName());

        Optional<Role> userRole = roleRepository.findRoleByRoleName("USER");
        user.setRoleId(userRole.orElseThrow(() -> new RoleDefinitionException("Role not found")));
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));

        userRepository.save(user);
    }

    @Override
    public Map<String, String> login(AuthenticationRequest authRequest) {
        Optional<User> registeredUsers = userRepository.findByEmail(authRequest.getEmail());
        if (registeredUsers.isPresent()) {
            if (passwordEncoder.matches(authRequest.getPassword(), registeredUsers.get().getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("firstName", registeredUsers.get().getFirstName());
                response.put("secondName", registeredUsers.get().getSecondName());
                response.put("email", registeredUsers.get().getEmail());
                response.put("token",
                        jwtTokenUtil.generateToken(
                                authRequest.getEmail(),
                                registeredUsers.get().getRoleId()
                        )
                );
                return response;
            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        } else {
            throw new IllegalArgumentException("The user with this email" + authRequest.getEmail() + "already exists");
        }
    }
    @Override
    public Optional<User> getUserId(Long userId){
        try {
            return userRepository.findByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserCreationException("Product with ID " + userId + " not found.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }
}
