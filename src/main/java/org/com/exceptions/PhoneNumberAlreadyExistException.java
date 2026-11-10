package org.com.exceptions;

public class PhoneNumberAlreadyExistException extends Exception {
    public PhoneNumberAlreadyExistException() {
        super("""                
                ?PhoneNumberAlreadyExist?
                Пользователь с таким номером телефона уже существует.
                """);
    }
}
