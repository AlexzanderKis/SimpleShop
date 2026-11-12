package org.com.service;

import org.com.domain.dto.UserLoginDTO;
import org.com.domain.dto.UserRegistrationDTO;
import org.com.domain.dto.UserResponseDTO;
import org.com.domain.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
/**
     Сервис пользователей
     Наследует UserDetailsService (Spring Security) - реализует loadUserByUsername, где username = email.
        используется при HTTP Basic аутентификации

     Возвращает DTO наружу, КРОМЕ getEntityByEmail - он нужен внутренним сервисам (OrderService и др.) для загрузки сущности пользователя
 */
public interface UserService extends UserDetailsService {
    UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO);

    // Проверяет учётные данные пользователя
    // ВАЖНО: не создаёт сессию и не выдаёт токен - только валидирует email/пароль
    UserResponseDTO login(UserLoginDTO userLoginDTO);

    Optional<UserResponseDTO> getUserById(String userId);

    Optional<UserResponseDTO> getUserByEmail(String email);

    Optional<UserResponseDTO> getUserByPhoneNumber(String phoneNumber);

    /**
         Возвращает сущность пользователя по email
         Используется внутренними сервисами (OrderService, CartService) и Spring Security
         Наружу в контроллеры НЕ отдаётся - использовать getUserByEmail() для DTO

         @param email email пользователя
         @return UserEntity
         @throws EntityNotFoundException если пользователь не найден

         намеренное нарушение инкапсуляции
         нужно для Spring Security и внутренних сервисов

         Если бы все методы возвращали только DTO, пришлось бы либо дублировать логику,
         либо делать лишние преобразования

         В реальных проектах для этого часто делают отдельный UserDetailsServiceImpl, чтобы не смешивать с бизнес-сервисом
     */
    UserEntity getEntityByEmail(String email);
}