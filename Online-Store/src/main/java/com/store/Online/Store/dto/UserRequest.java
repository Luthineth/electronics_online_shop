package com.store.Online.Store.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String firstName;

    private String secondName;

    private String email;

    private String password;
}