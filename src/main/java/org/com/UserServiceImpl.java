package org.com;

import static org.com.tools.RUPhoneNumberValidator.validateNumber;
import static org.com.tools.PasswordValidator.validatePassword;

import org.apache.commons.validator.routines.EmailValidator;

import org.com.exceptions.*;
import org.com.repos.UserRepo;
import org.com.service.UserService;
import org.com.tools.PasswordEncoder;

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

    // регистрирует и валидирует данные
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
            user.setUserPassword(PasswordEncoder.hashPassword(password));
            userRepo.save(user);
            System.out.println("User saved");
        } else {
            throw new InvalidPasswordException("password incorrect"); // password incorrect
        }
        System.out.println("Registration successfully done");
        return user;
    }

    // аутентифицирует и обновляет текущую сессию
    @Override
    public User login(String email, String password) throws UserLoginException {
        Optional<User> userToLogin = userRepo.findByEmail(email);

        if (userToLogin.isPresent() && PasswordEncoder.hashMatchesPassword(password, userToLogin.get().getUserPassword())) {
            System.out.println("user exist");
            System.out.println("password correct");
            this.currentUser = userToLogin.get();
        } else {
            throw new UserLoginException("user email no exist or password incorrect");
        }
        return currentUser;
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