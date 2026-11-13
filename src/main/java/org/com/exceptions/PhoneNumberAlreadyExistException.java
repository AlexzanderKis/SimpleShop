package org.com.exceptions;

public class PhoneNumberAlreadyExistException extends RuntimeException {
    public PhoneNumberAlreadyExistException(String message) {
        super(String.format("""                
                ?PhoneNumberAlreadyExist?
                Пользователь с номером телефона %s уже существует.
                """, message));
    }
}
