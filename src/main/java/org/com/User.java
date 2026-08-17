package org.com;

import org.com.repos.UserRepo;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userId;
    private final String userName;
    private final String userEmail;
    private final String userPhoneNumber;
    private final Cart userCart;
    private final List<Order> userOrder;

    public User(String userId,
                String userName,
                String userEmail,
                String userPhoneNumber,
                Cart userCart,
                List<Order> userOrder) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userCart = userCart;
        this.userOrder = userOrder;
    }

    public User(String userId,
                String userName,
                String userEmail,
                String userPhoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userCart = null;
        this.userOrder = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public Cart getUserCart() {
        return userCart;
    }

    public List<Order> getUserOrders() {
        return userOrder;
    }
}