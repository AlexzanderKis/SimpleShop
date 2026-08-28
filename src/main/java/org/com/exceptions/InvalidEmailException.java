package org.com.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("""
                ?EmailIncorrect?
                    Valid email address:
                    email@example.com
                    firstname.lastname@example.com
                    firstname+lastname@example.com
                    email@subdomain.example.com
                    1234567890@example.com""");
    }
}