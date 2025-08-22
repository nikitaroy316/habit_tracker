package com.nikita.habittracker.mapper;

import com.nikita.habittracker.dto.ProfileDTO;
import com.nikita.habittracker.model.Profile;

public class ProfileMapper {
    public static ProfileDTO toDTO(Profile profile)
    {
        if (profile == null) return null;

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFirstName(profile.getFirstName());
        profileDTO.setLastName(profile.getLastName());
        profileDTO.setBio(profile.getBio());

        return profileDTO;
    }

    public static Profile toEntity(ProfileDTO profileDTO)
    {
        if(profileDTO == null)
            return null;

        Profile profile = new Profile();
        profile.setLastName(profileDTO.getLastName());
        profile.setFirstName(profileDTO.getFirstName());
        profile.setBio(profileDTO.getBio());

        return profile;
    }
}
