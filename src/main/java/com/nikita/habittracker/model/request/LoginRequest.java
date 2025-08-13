package com.nikita.habittracker.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String emailAddress;
    private String password;
}
