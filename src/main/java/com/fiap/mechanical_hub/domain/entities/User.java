package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.exceptions.UserAlreadyDeletedException;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {

    private UUID id;
    private String name;
    private String email;
    private String documentNumber;
    private String passwordHash;
    private Profile profile;
    private LocalDateTime deletedAt;

    public User() {}

    public User(UUID id, String name, String email, String documentNumber, String passwordHash, Profile profile, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.documentNumber = documentNumber;
        this.passwordHash = passwordHash;
        this.profile = profile;
        this.deletedAt = deletedAt;
    }

    public static User create(
            String name,
            String email,
            String documentNumber,
            String passwordHash,
            Profile profile
    ) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.name = name;
        user.email = email;
        user.documentNumber = new Document(DocumentTypeEnum.CPF, documentNumber).getNumber();
        user.passwordHash = passwordHash;
        user.profile = profile;

        return user;
    }

    public static User build(
            UUID id,
            String name,
            String email,
            String documentNumber,
            String passwordHash,
            Profile profile
    ) {
        User user = new User();

        user.id = id;
        user.name = name;
        user.email = email;
        user.documentNumber = documentNumber;
        user.passwordHash = passwordHash;
        user.profile = profile;

        return user;
    }

    public void deactivate() {
        if (isDeleted()) {
            throw new UserAlreadyDeletedException(this.id.toString());
        }
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isActive() {
        return this.deletedAt == null;
    }
}
