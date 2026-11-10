package org.com.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("""
                ?EmailIncorrect?
                Некорректный формат email
                Пример правильного:
                    email@example.com
                    firstname.lastname@example.com
                    firstname+lastname@example.com
                    email@subdomain.example.com
                    1234567890@example.com""");
    }
}