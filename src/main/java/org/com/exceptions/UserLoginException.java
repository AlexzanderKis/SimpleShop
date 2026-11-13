package org.com.exceptions;

public class UserLoginException extends RuntimeException{
    public UserLoginException() {
        super("Неверный email или пароль.");
    }
}
