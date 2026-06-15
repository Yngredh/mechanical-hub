package com.fiap.mechanical_hub.infrastructure.security;

import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserSecurityAdapterTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
    private static final String USER_EMAIL = "joao@email.com";
    private static final String PASSWORD_HASH = "hashed_password_123";

    private static User userWithProfile(ProfileEnum profileEnum) {
        Profile profile = Profile.create(profileEnum);
        return User.build(USER_ID, "João Silva", USER_EMAIL, PASSWORD_HASH, profile);
    }

    @Test
    void shouldReturnRolePrefixedWithProfileName_whenUserIsAdministrator() {
        UserSecurityAdapter adapter = new UserSecurityAdapter(userWithProfile(ProfileEnum.ADMINISTRATOR));

        String authority = adapter.getAuthorities().iterator().next().getAuthority();

        assertThat(authority).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnRolePrefixedWithProfileName_whenUserIsMechanical() {
        UserSecurityAdapter adapter = new UserSecurityAdapter(userWithProfile(ProfileEnum.MECHANICAL));

        String authority = adapter.getAuthorities().iterator().next().getAuthority();

        assertThat(authority).isEqualTo("ROLE_MECANICO");
    }

    @Test
    void shouldReturnUserEmail_whenGettingUsername() {
        UserSecurityAdapter adapter = new UserSecurityAdapter(userWithProfile(ProfileEnum.ADMINISTRATOR));

        String username = adapter.getUsername();

        assertThat(username).isEqualTo(USER_EMAIL);
    }

    @Test
    void shouldReturnPasswordHash_whenGettingPassword() {
        UserSecurityAdapter adapter = new UserSecurityAdapter(userWithProfile(ProfileEnum.ADMINISTRATOR));

        String password = adapter.getPassword();

        assertThat(password).isEqualTo(PASSWORD_HASH);
    }
}
