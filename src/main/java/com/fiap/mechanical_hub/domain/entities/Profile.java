package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Profile {

    private UUID id;
    private String name;
    private String description;

    public static Profile create(ProfileEnum profileEnum) {
        Profile profile = new Profile();
        profile.id = UUID.randomUUID();
        profile.name = profileEnum.name();
        profile.description = profileEnum.getDescription();
        return profile;
    }

}
