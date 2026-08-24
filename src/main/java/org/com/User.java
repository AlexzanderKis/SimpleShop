package org.com;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userId;
    private final String userName;
    private final String userEmail;
    private final String userPhoneNumber;
    private String userPassword;
    private final Cart userCart;
    private final List<Order> userOrder;

    public User(String userId,
                String userName,
                String userEmail,
                String userPhoneNumber, String userPassword,
                Cart userCart,
                List<Order> userOrder) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userCart = userCart;
        this.userOrder = userOrder;
    }

    public User(String userId,
                String userName,
                String userEmail,
                String userPhoneNumber,
                String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userCart = null;
        this.userOrder = new ArrayList<>();
    }

    public User(String userId, String userName, String userEmail, String userPhoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = null;
        this.userCart = null;
        this.userOrder = new ArrayList<>();
    }

    public User(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userId = null;
        this.userName = null;
        this.userPhoneNumber = null;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Cart getUserCart() {
        return userCart;
    }

    public List<Order> getUserOrders() {
        return userOrder;
    }
}