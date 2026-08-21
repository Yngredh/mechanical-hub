package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryMapperTest {

    private static final UUID PROFILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000200");

    private ProfileModel buildProfileModel(ProfileEnum profile) {
        return new ProfileModel(PROFILE_ID, profile.name(), profile.getDescription());
    }

    private UserModel buildUserModel(User user, ProfileModel profileModel) {
        UserModel model = new UserModel();
        model.setId(user.getId());
        model.setName(user.getName());
        model.setEmail(user.getEmail());
        model.setDocumentNumber(user.getDocumentNumber());
        model.setPasswordHash(user.getPasswordHash());
        model.setProfile(profileModel);
        model.setDeletedAt(user.getDeletedAt());
        return model;
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToModel() {
        User user = UserMock.active();
        ProfileModel profileModel = buildProfileModel(ProfileEnum.ADMINISTRATOR);

        UserModel model = UserRepositoryMapper.toModel(user, profileModel);

        assertThat(model.getId()).isEqualTo(user.getId());
        assertThat(model.getName()).isEqualTo(user.getName());
        assertThat(model.getEmail()).isEqualTo(user.getEmail());
        assertThat(model.getDocumentNumber()).isEqualTo(user.getDocumentNumber());
        assertThat(model.getPasswordHash()).isEqualTo(user.getPasswordHash());
        assertThat(model.getProfile()).isEqualTo(profileModel);
        assertThat(model.getDeletedAt()).isEqualTo(user.getDeletedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingModelToDomain() {
        User user = UserMock.active();
        ProfileModel profileModel = buildProfileModel(ProfileEnum.ADMINISTRATOR);
        UserModel model = buildUserModel(user, profileModel);

        User domain = UserRepositoryMapper.toDomain(model);

        assertThat(domain.getId()).isEqualTo(model.getId());
        assertThat(domain.getName()).isEqualTo(model.getName());
        assertThat(domain.getEmail()).isEqualTo(model.getEmail());
        assertThat(domain.getDocumentNumber()).isEqualTo(model.getDocumentNumber());
        assertThat(domain.getPasswordHash()).isEqualTo(model.getPasswordHash());
        assertThat(domain.getDeletedAt()).isEqualTo(model.getDeletedAt());
    }

    @Test
    void shouldPreserveDeletedAt_whenConvertingInactiveUserToModel() {
        User deletedUser = UserMock.deleted();
        ProfileModel profileModel = buildProfileModel(ProfileEnum.ADMINISTRATOR);

        UserModel model = UserRepositoryMapper.toModel(deletedUser, profileModel);

        assertThat(model.getDeletedAt()).isNotNull();
    }
}
