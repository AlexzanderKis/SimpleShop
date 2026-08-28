package org.com.exceptions;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException() {
        super("""
                ?PhoneNumberIncorrect?
                    Valid phone number format:
                    +7 (911) 123-45-67
                    84951234567
                    7-495-123-45-67""");
    }
}
