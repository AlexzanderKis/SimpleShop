package org.com.tools;

import org.com.exceptions.InvalidPhoneNumberException;

import java.util.regex.Pattern;

public class RUPhoneNumberValidator {
    private static final Pattern PATTERN = Pattern
            .compile("^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$");

    public static boolean validateNumber(String phoneNumber) {
        if (PATTERN.matcher(phoneNumber).matches()){
            return true;
        } throw new InvalidPhoneNumberException();
//        return PATTERN.matcher(phoneNumber).matches();
    }
}