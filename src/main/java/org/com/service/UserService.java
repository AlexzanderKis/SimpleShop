package org.com.service;

import org.com.User;
import org.com.exceptions.*;

import java.util.Optional;

public interface UserService {
    void registerUser(String name, String phoneNumber, String email, String password)
            throws Exception;

    void login(String email, String password) throws UserLoginException;

    Optional<User> getCurrentUser();

    Optional<User> getUserById(String userId);

    Optional<User> getUserByPhoneNumber(String phoneNumber);

    Optional<User> getUserByEmail(String email);
}