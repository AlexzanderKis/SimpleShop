package org.com.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.com.domain.dto.UserLoginDTO;
import org.com.domain.dto.UserRegistrationDTO;
import org.com.domain.dto.UserResponseDTO;
import org.com.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
         REST-контроллер аутентификации.
         Обслуживает JSON-запросы на регистрацию /api/v1/auth/register и логин /api/v1/auth/login
         Используется внешними клиентами (мобильные приложения, SPA, Postman)
         Для HTML-страниц логина/регистрации смотреть WebAuthController
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Аутентификация и Регистрация", description = "Регистрация новых пользователей с валидацией РФ-телефона, email и пароля, а также вход в систему")
public class AuthRestController {
    private final UserService userService;

    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    // принимает JSON с данными, вызывает UserService, возвращает JSON с данными пользователя без пароля
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создание учетной записи с валидацией формата номера РФ, уникальности email/телефона и надежности пароля")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO){
        UserResponseDTO createdUser = userService.registerUser(userRegistrationDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
        Проверяет учётные данные пользователя
        !ВАЖНО! НЕ устанавливает HTTP-сессию и НЕ выдаёт токен - это не «логин» в смысле Spring Security

        Метод только валидирует email/пароль через PasswordEncoder и возвращает данные пользователя

        TODO: Если в будущем появится JWT - здесь будет точка выдачи токена
     */
    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя", description = "Проверка учетных данных пользователя (email и пароль)")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }
}