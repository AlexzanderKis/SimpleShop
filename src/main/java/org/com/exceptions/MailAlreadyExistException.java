package org.com.exceptions;

public class MailAlreadyExistException extends Exception {
    public MailAlreadyExistException(String message) {
        super(message);
    }
}
