package org.com.exceptions;

public class InvalidPhoneNumberException extends Exception {
    public InvalidPhoneNumberException(String message){
        super(message);
    }
}
