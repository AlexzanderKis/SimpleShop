package org.com.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.com.domain.dto.UserResponseDTO;
import org.com.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
        REST-контроллер пользователей
        Обслуживает JSON-запросы к /api/v1/users/**
        Реализован один метод - получение профиля текущего аутентифицированного пользователя
        TODO: Для HTML-страниц профиля (если появятся) создать Web-контроллер
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Пользователи", description = "Управление профилем пользователя")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // возвращает данные текущего аутентифицированного пользователя (имя, email, телефон, роль) в виде JSON
    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя", description = "Возвращает данные профиля авторизованного пользователя (имя, email, телефон, роль)")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        return userService.getUserByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
