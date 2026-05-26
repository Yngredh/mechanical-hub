package com.fiap.mechanical_hub.infrastructure.service;

import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.infrastructure.database.mappers.UserRepositoryMapper;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByEmail(email);
        if (userModel == null) throw new UsernameNotFoundException("User not found: " + email);

        User user = UserRepositoryMapper.toDomain(userModel);
        return new UserSecurityAdapter(user);
    }
}
