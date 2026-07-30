package org.com.repos;

import org.com.User;

import java.util.Optional;

public interface UserRepo {
    Optional<User> findById();

    Optional<User> findByEmail();

    //    Optional<User> findByPhoneNumber();

    void save(User user);
}