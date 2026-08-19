package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.user.UserResponse;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.utils.document.DocumentFormatter;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                formatDocument(user.getDocumentNumber()),
                user.getProfile().getName()
        );
    }

    private String formatDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            return null;
        }
        return DocumentFormatter.formatDocument(DocumentTypeEnum.CPF.getValue(), documentNumber);
    }
}

