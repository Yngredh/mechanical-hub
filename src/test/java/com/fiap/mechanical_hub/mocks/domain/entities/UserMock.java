package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;

import java.util.UUID;

public class UserMock {

    public static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");

    public static User active() {
        return User.build(USER_ID, "João Silva", "joao@email.com", "hashed_password_123", new Profile());
    }

    public static User deleted() {
        User user = active();
        user.deactivate();
        return user;
    }
}
