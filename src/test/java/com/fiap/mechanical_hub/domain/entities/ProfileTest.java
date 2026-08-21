package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTest {

    @Test
    void shouldCreateProfile_fromProfileEnum() {
        Profile profile = Profile.create(ProfileEnum.ADMINISTRATOR);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getName()).isEqualTo("ADMINISTRATOR");
    }

    @Test
    void shouldCreateProfileWithDifferentEnumValues() {
        Profile mechanicProfile = Profile.create(ProfileEnum.MECHANICAL);

        assertThat(mechanicProfile.getId()).isNotNull();
        assertThat(mechanicProfile.getName()).isEqualTo("MECHANICAL");
    }

    @Test
    void shouldMaintainUniqueIdPerProfile() {
        Profile profile1 = Profile.create(ProfileEnum.ADMINISTRATOR);
        Profile profile2 = Profile.create(ProfileEnum.ADMINISTRATOR);

        assertThat(profile1.getId()).isNotEqualTo(profile2.getId());
    }

    @Test
    void shouldHaveDescriptionForEachProfile() {
        Profile profile = Profile.create(ProfileEnum.ADMINISTRATOR);

        assertThat(profile.getDescription()).isNotNull();
    }
}

