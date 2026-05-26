package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.user.UserResponse;
import com.fiap.mechanical_hub.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserHttpMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile().getName()
        );
    }
}


