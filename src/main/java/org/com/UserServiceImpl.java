package org.com;

import static org.com.tools.RUPhoneNumberValidator.validateNumber;
import static org.com.tools.PasswordValidator.validatePassword;

import org.apache.commons.validator.routines.EmailValidator;
import org.com.exceptions.*;
import org.com.repos.UserRepo;
import org.com.service.UserService;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private User currentUser;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    @Override
    public User registerUser(String name, String phoneNumber, String email, String password)
            throws Exception {

        User user = new User(UUID.randomUUID().toString(),
                name,
                email,
                phoneNumber);

        if (EmailValidator.getInstance().isValid(email)) {
            if (userRepo.findByEmail(email).isPresent()) {
                throw new MailAlreadyExistException("email already exist");
            }
        } else {
            throw new InvalidEmailException("Invalid email"); // "email incorrect"
        }

        if (validateNumber(phoneNumber)) {
            if (userRepo.findByPhoneNumber(phoneNumber).isPresent()) {
                throw new PhoneNumberAlreadyExistException("phone number already exist");
            }
        } else {
            throw new InvalidPhoneNumberException("incorrect phone number"); // phone num incorrect
        }

        if (validatePassword(password)) {
            userRepo.save(user);
            System.out.println("User saved");
        } else {
            throw new InvalidPasswordException("password incorrect"); // password incorrect
        }
        System.out.println("Registration successfully done");
        return user;
    }

    @Override
    public Optional<User> login(String email, String password) {
        User user = new User(currentUser.getUserId(),
                currentUser.getUserName(),
                currentUser.getUserEmail(),
                currentUser.getUserPhoneNumber());
        if (userRepo.findByEmail(email).isPresent()) {
            System.out.println();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepo.findById(userId);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String userPhoneNumber) {
        return userRepo.findByPhoneNumber(userPhoneNumber);
    }

    @Override
    public Optional<User> getUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail);
    }
}