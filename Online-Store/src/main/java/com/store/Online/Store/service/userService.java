package com.store.Online.Store.service;


import com.store.Online.Store.dto.AuthenticationRequest;
import com.store.Online.Store.dto.UserRequest;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.RoleDefinitionException;
import com.store.Online.Store.exception.UserCreationException;

import java.util.Map;
import java.util.Optional;

public interface userService {

    void register(UserRequest user) throws RoleDefinitionException, UserCreationException;

    Map<String,String> login(AuthenticationRequest authRequest);
}
