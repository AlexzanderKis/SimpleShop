package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Учетные данные для входа (почта и пароль)")
public class UserLoginDTO {

    @Schema(description = "Электронная почта", example = "user@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Schema(description = "Пароль", example = "Qwerty123@")
    @NotBlank(message = "Пароль обязателен")
    private String password;

    public UserLoginDTO() { }

    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}