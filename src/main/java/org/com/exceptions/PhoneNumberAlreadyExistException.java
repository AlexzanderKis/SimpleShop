package org.com.exceptions;

public class PhoneNumberAlreadyExistException extends Exception {
    public PhoneNumberAlreadyExistException() {
        super("Phone number already exist");
    }
}
