package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;

import java.util.UUID;

public class UserModelMock {

    public static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
    public static final UUID PROFILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000200");
    public static final String DOCUMENT_NUMBER = "52998224725";

    public static UserModel active() {
        ProfileModel profile = new ProfileModel(PROFILE_ID, "ADMINISTRATOR", "Administrador");
        UserModel model = new UserModel();
        model.setId(USER_ID);
        model.setName("João Silva");
        model.setEmail("joao@email.com");
        model.setDocumentNumber(DOCUMENT_NUMBER);
        model.setPasswordHash("hashed_password_123");
        model.setProfile(profile);
        model.setDeletedAt(null);
        return model;
    }
}
