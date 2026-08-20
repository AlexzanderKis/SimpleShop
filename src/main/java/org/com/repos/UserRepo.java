package org.com.repos;

import org.com.User;

import java.util.Optional;

public interface UserRepo {
    Optional<User> findById(String userId);

    Optional<User> findByEmail(String userEmail);

    Optional<User> findByPhoneNumber(String userPhoneNumber);

    void save(User user);

    void deleteUser(User user);
}