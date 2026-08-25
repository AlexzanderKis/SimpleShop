package org.com;

import static org.com.tools.RUPhoneNumberValidator.validateNumber;
import static org.com.tools.PasswordValidator.validatePassword;

import org.apache.commons.validator.routines.EmailValidator;

import org.com.exceptions.*;
import org.com.repos.UserRepo;
import org.com.service.UserService;
import org.com.tools.PasswordEncoder;

import java.util.Locale;
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
    public void registerUser(String name, String phoneNumber, String email, String password)
            throws Exception {

        User user = new User(UUID.randomUUID().toString().substring(0,12).toUpperCase(Locale.ROOT),
                name,
                email,
                phoneNumber);

        if (EmailValidator.getInstance().isValid(email)) {
            if (userRepo.findByEmail(email).isPresent()) {
                throw new MailAlreadyExistException("?EmailAlreadyExist?");
            }
        } else {
            throw new InvalidEmailException("?InvalidEmail?"); // "email incorrect"
        }

        if (validateNumber(phoneNumber)) {
            if (userRepo.findByPhoneNumber(phoneNumber).isPresent()) {
                throw new PhoneNumberAlreadyExistException("?PhoneNumberAlreadyExist?");
            }
        } else {
            throw new InvalidPhoneNumberException("?IncorrectPhoneNumber?"); // phone num incorrect
        }

        if (validatePassword(password)) {
            user.setUserPassword(PasswordEncoder.hashPassword(password));
            userRepo.save(user);
            System.out.println("?UserSaved?");
        } else {
            throw new InvalidPasswordException("?PasswordIncorrect?"); // password incorrect
        }
        System.out.println("?Registration successfully done?");
    }

    // аутентифицирует и обновляет текущую сессию
    @Override
    public void login(String email, String password) throws UserLoginException {
        Optional<User> userToLogin = userRepo.findByEmail(email);

        if (userToLogin.isPresent() && PasswordEncoder.hashMatchesPassword(password, userToLogin.get().getUserPassword())) {
            System.out.print("""
                            ?UserExist?
                    """);
            System.out.print("""
                            ?PasswordCorrect?
                    """);
            this.currentUser = userToLogin.get();
        } else {
            throw new UserLoginException("?user email no exist or password incorrect?");
        }
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