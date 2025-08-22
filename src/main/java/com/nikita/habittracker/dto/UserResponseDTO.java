package com.nikita.habittracker.dto;

import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private int id;
    private String userName;
    private String email;
    private Role role;
    private Profile profile;
    private boolean enabled;
    private boolean accountNonLocked;
}
