package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.infrastructure.database.mappers.UserMapper;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationUseCase implements UserDetailsService {

    private final UserJpaRepository userRepository;
    private final ProfileJpaRepository profileRepository;

    public AuthorizationUseCase(UserJpaRepository userRepository, ProfileJpaRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByEmail(email);
        if (userModel == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        User user = UserMapper.toDomain(userModel);
        return new UserSecurityAdapter(user);
    }

    public void registerNewUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.login()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        ProfileModel profile = profileRepository.findByName(request.profile());
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found: " + request.profile());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User newUser = User.create(
                request.name(),
                request.login(),
                encryptedPassword,
                Profile.create(ProfileEnum.valueOf(request.profile()))
        );

        this.userRepository.save(UserMapper.toModel(newUser, profile));
    }
}
