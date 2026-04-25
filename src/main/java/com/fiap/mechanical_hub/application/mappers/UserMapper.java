package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;

public class UserMapper {

    public static User toDomain(UserModel model) {
        return User.build(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getPasswordHash(),
                Profile.create(ProfileEnum.valueOf(model.getProfile().getName()))
        );
    }

    public static UserModel toModel(User user, ProfileModel profile) {
        UserModel model = new UserModel();

        model.setId(user.getId());
        model.setName(user.getName());
        model.setEmail(user.getEmail());
        model.setPasswordHash(user.getPasswordHash());
        model.setProfile(profile);

        return model;
    }
}