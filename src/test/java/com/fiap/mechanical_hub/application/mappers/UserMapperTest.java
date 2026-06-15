package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.user.UserResponse;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private static User userWithProfile(ProfileEnum profileEnum) {
        Profile profile = Profile.create(profileEnum);
        return User.build(UserMock.USER_ID, "João Silva", "joao@email.com", "hashed_password_123", profile);
    }

    @Test
    void shouldMapIdNameAndEmail_whenConvertingToResponse() {
        User user = userWithProfile(ProfileEnum.ADMINISTRATOR);

        UserResponse response = mapper.toResponse(user);

        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.name()).isEqualTo(user.getName());
        assertThat(response.email()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldMapProfileName_whenConvertingToResponse() {
        User user = userWithProfile(ProfileEnum.ADMINISTRATOR);

        UserResponse response = mapper.toResponse(user);

        assertThat(response.profile()).isEqualTo(ProfileEnum.ADMINISTRATOR.getDisplayName());
    }

    @Test
    void shouldMapMechanicalProfileName_whenUserIsMechanical() {
        User user = userWithProfile(ProfileEnum.MECHANICAL);

        UserResponse response = mapper.toResponse(user);

        assertThat(response.profile()).isEqualTo(ProfileEnum.MECHANICAL.getDisplayName());
    }
}
