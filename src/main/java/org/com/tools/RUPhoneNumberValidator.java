package org.com.tools;

import org.com.repos.UserRepo;

import java.util.regex.Pattern;

public class RUPhoneNumberValidator {
    static UserRepo userRepo;
    private static final Pattern PATTERN = Pattern
            .compile("^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$");

    public static boolean validateNumber(String phoneNumber) {
//        if (userRepo.findByPhoneNumber(phoneNumber).isPresent()){
//            System.out.println("phNum exist");
//            return false;
//        }
        return PATTERN.matcher(phoneNumber).matches();
    }
}