package org.com.exceptions;

public class MailAlreadyExistException extends RuntimeException {
    public MailAlreadyExistException() {
        super("""
                ?EmailAlreadyExist?
                Пользователь с таким адресом электронной почты уже зарегистрирован.
                """);
    }
}
