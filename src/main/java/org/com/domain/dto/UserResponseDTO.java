package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.Role;

import java.time.LocalDateTime;

// Response то, что уходит клиенту
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Schema(description = "Информация о пользователе (id, name, email, phoneNumber, role, registrationDate)")
public class UserResponseDTO {

    @Schema(description = "Идентификатор пользователя", example = "USER-DEMO-001")
    private String id;

    @Schema(description = "ФИО", example = "Иван Петров")
    private String name;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Schema(description = "Телефон", example = "+7 (903) 111-55-99")
    private String phoneNumber;

    @Schema(description = "Роль в системе", example = "ROLE_USER")
    private Role role;

    @Schema(description = "Дата регистрации")
    private LocalDateTime registrationDate;
}
