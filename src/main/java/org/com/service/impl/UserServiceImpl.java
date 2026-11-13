package org.com.service.impl;

import org.apache.commons.validator.routines.EmailValidator;

import org.com.domain.dto.UserLoginDTO;
import org.com.domain.dto.UserRegistrationDTO;
import org.com.domain.dto.UserResponseDTO;
import org.com.domain.entity.UserEntity;
import org.com.domain.entity.enums.Role;
import org.com.exceptions.*;
import org.com.repos.UserRepo;
import org.com.service.UserService;
import org.com.validation.PasswordValidator;
import org.com.validation.RUPhoneNumberValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_ID_HEAD = "USR-";

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // регистрирует и валидирует данные
    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO) {

        RUPhoneNumberValidator.validateNumber(userRegistrationDTO.getPhoneNumber());

// PH NUM VALIDATION
        if (userRepo.existsByPhoneNumber(userRegistrationDTO.getPhoneNumber().trim())) {
            throw new PhoneNumberAlreadyExistException(userRegistrationDTO.getPhoneNumber());
        }

// EMAIL VALIDATION
        if (!EmailValidator.getInstance().isValid(userRegistrationDTO.getEmail())) {
            throw new InvalidEmailException();
        }

        if (userRepo.existsByEmail(userRegistrationDTO.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new MailAlreadyExistException();
        }

// PASSWORD VALIDATION
        PasswordValidator.validatePassword(userRegistrationDTO.getPassword());

// CREATE ENTITY
        String userId = USER_ID_HEAD + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        String encodedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());
        UserEntity user = new UserEntity(
                userId,
                userRegistrationDTO.getName().trim(),
                userRegistrationDTO.getEmail().trim().toLowerCase(Locale.ROOT),
                userRegistrationDTO.getPhoneNumber().trim(),
                encodedPassword,
                Role.ROLE_USER);

        return mapToDTO(user);
    }

    // аутентифицирует и обновляет текущую сессию
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO login(UserLoginDTO userLoginDTO) {
        UserEntity user = userRepo.findByEmail(userLoginDTO.getEmail().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(UserLoginException::new);

        // проверка совпадения пароля
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPasswordHash()))){
            throw new UserLoginException();
        }
        return mapToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserById(String userId) {
        return userRepo.findById(userId).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserByPhoneNumber(String userPhoneNumber) {
        return userRepo.findByPhoneNumber(userPhoneNumber.trim()).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail.toLowerCase(Locale.ROOT)).map(this::mapToDTO);
    }

    /**
             Возвращает Entity пользователя по email
             НАМЕРЕННО нарушает правило «наружу только DTO», потому что:
             - используется внутренними сервисами (OrderServiceImpl) для загрузки UserEntity,
             - используется Spring Security (через loadUserByUsername)

             ! НЕ вызывать из контроллеров !

             Для внешних вызовов -> getUserByEmail()
     */
    @Override
    @Transactional(readOnly = true)
    public UserEntity getEntityByEmail(String email) {
        return userRepo.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + email + " не найден"));
    }

    /**
             Spring Security вызывает этот метод при HTTP Basic аутентификации.
             username - это email (так настроено в SecurityConfig).
             Возвращается org.springframework.security.core.userdetails.User - стандартная реализация UserDetails.
             SimpleGrantedAuthority(user.getRole().name()) - роль в формате ROLE_USER или ROLE_ADMIN.
             Spring Security ожидает префикс ROLE_, и он уже есть в enum Role.

             Зачем это: клиент присылает Authorization: Basic base64(email:password),
             Spring Security:
             Декодирует, получает email и пароль.
             Вызывает loadUserByUsername(email).
             Сравнивает passwordEncoder.matches(rawPassword, userDetails.getPassword()).
             Если совпало создаёт Authentication с ролями из UserDetails.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(username.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return new User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));
    }

    // маппинг Entity to DTO
    private UserResponseDTO mapToDTO(UserEntity entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getRole(),
                entity.getRegistrationDate());
    }
}