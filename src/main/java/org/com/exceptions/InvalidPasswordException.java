package org.com.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(){
        super("""
                "?PasswordIncorrect?"
                    Ensure there is at least one lowercase letter.
                    Ensure there is at least one uppercase letter.
                    Ensure there is at least one number digit.
                    Ensure there is at least one special character from set [@#$%^&+=].
                    Requires the total length of the password to be 8 or more characters.""");
    }
}
