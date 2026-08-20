package org.com.service;

import org.com.User;
import org.com.exceptions.*;

import java.util.Optional;

public interface UserService {
    User registerUser(String name, String phoneNumber, String email, String password)
            throws Exception;

    User login(String email, String password) throws UserLoginException;

    Optional<User> getCurrentUser();

    Optional<User> getUserById(String userId);

    Optional<User> getUserByPhoneNumber(String phoneNumber);

    Optional<User> getUserByEmail(String email);
}