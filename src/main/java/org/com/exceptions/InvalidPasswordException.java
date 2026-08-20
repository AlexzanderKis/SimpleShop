package org.com.exceptions;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message){
        super(message);
    }
}
