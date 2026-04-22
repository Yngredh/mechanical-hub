package com.fiap.mechanical_hub.domain.entities;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {

    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private Profile profile;

    public static User create(
            String name,
            String email,
            String passwordHash,
            Profile profile
    ) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.profile = profile;

        return user;
    }

    public static User build(
            UUID id,
            String name,
            String email,
            String passwordHash,
            Profile profile
    ) {
        User user = new User();

        user.id = id;
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.profile = profile;

        return user;
    }
}
