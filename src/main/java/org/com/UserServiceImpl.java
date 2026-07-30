package org.com;

import org.com.repos.UserRepo;
import org.com.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public UserServiceImpl(User user, UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    @Override
    public User registerUser(String name, String phoneNumber, String email, String password) {
//        String name = "Achilles";
//        String phoneNumber = "89997776655";
//        String email = "allfreetome@gmail.com";
//        String password = "qwerty123";

        return new User();
    }

    @Override
    public Optional<User> login(String email, String password) {
        return Optional.empty();
    }

    @Override
    public User getCurrentUser() {
        return userRepo.findByEmail().get();
    }
}
