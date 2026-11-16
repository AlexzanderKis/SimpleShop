package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Данные для регистрации нового пользователя (имя, почта, телефон, пароль)")
public class UserRegistrationDTO {

    @Schema(description = "Имя пользователя", example = "Иван Петров")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 200, message = "Имя должно быть от 2 до 200 символов")
    private String name;

    @Schema(description = "Электронная почта", example = "ivan@example.com")
    @Email(message = "Email обязателен")
    @NotBlank(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Номер телефона РФ (+7 / 8)", example = "+7 (903) 111-55-99")
    @NotBlank(message = "Номер телефона обязателен")
    private String phoneNumber;

    @Schema(description = "Пароль (мин. 8 символов, заглавная, строчная, цифра, спецсимвол)", example = "Qwerty123@")
    @NotBlank(message = "Пароль обязателен")
    private String password;

    public UserRegistrationDTO() { }

    public UserRegistrationDTO(String name, String email, String phoneNumber, String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
