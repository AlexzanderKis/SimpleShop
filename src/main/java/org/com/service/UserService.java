package org.com.service;

import org.com.User;

import java.util.Optional;

public interface UserService {
    User registerUser(String name, String phoneNumber, String email, String password);

    Optional<User> login(String email, String password);

    User getCurrentUser();
}
