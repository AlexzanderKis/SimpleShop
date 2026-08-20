package org.com;

import org.com.repos.UserRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//  класс, внутри которого данные хранятся в Map (в оперативной памяти)
public class InMemoryUserRepo implements UserRepo {

    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String userEmail) {
        return userMap.values().stream()
                .filter(user -> user.getUserEmail().equals(userEmail))
                .findFirst();
    }

    @Override
    public Optional<User> findByPhoneNumber(String userPhoneNumber) {
        return userMap.values().stream()
                .filter(user -> user.getUserPhoneNumber().equals(userPhoneNumber))
                .findFirst();
    }

    @Override
    public void save(User user) {
        userMap.put(user.getUserId(), user);
    }

    @Override
    public void deleteUser(User user) {
        userMap.remove(user.getUserId());
    }
}