package com.nikita.habittracker.mapper;

import com.nikita.habittracker.dto.UserRequestDTO;
import com.nikita.habittracker.dto.UserResponseDTO;
import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        if (dto.getProfile() != null) {
            Profile profile = new Profile();
            profile.setFirstName(dto.getProfile().getFirstName());
            profile.setLastName(dto.getProfile().getLastName());
            profile.setBio(dto.getProfile().getBio());
            user.setProfile(profile);
        }

        return user;
    }

    public static UserResponseDTO toDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfile(user.getProfile());
        dto.setEnabled(user.isEnabled());
        dto.setAccountNonLocked(user.isAccountNonLocked());
        return dto;
    }
}
