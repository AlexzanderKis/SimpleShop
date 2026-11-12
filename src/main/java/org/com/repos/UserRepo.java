package org.com.repos;

import org.com.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// стандартные методы для поиска и проверки существования пользователя

/**
    Репозиторий пользователей
    поиск по email/телефону (возвращает Optional, чтобы явно показать возможность null) и проверку существования (existsBy)
    ID пользователя - строковый (генерируется приложением), поэтому JpaRepository<UserEntity, String>
 */
@Repository
public interface UserRepo extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    //    Optional<UserEntity> findById(String userId);

//    void save(UserEntity user);

//    void deleteUser(UserEntity user);
}