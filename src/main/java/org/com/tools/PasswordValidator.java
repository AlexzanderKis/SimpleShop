package org.com.tools;

import java.util.regex.Pattern;

public class PasswordValidator {
// new validator
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

/** // old weird validator
    static final int PASS_MAX_LENGTH = 8;
    static final int PASS_MIN_LENGTH = 3;
    static final int SYMBOL_MAX_REPEATS = 3;
    private int inputLength;
    private int inputMaxRepeats;

    public static int getPASS_MIN_LENGTH() {
        return PASS_MIN_LENGTH;
    }
    public static int getPASS_MAX_LENGTH() {
        return PASS_MAX_LENGTH;
    }
    public static int getSYMBOL_MAX_REPEATS() {
        return SYMBOL_MAX_REPEATS;
    }
    public int getInputMaxRepeats() {
        return inputMaxRepeats;
    }
    public int getInputLength() {
        return inputLength;
    }

    //    public void setMinLength(int inputLength) {
    //        if (inputLength > PASS_MIN_LENGTH) {
    //            this.inputLength = inputLength;
    //            System.out.println("""
    //                    Valid password length
    //                    """);
    //        } else {
    //            throw new IllegalArgumentException("Password length number mustn't be negative or quantity of symbols less than "+PASS_MIN_LENGTH);
    //        }
    //    }
    //
    //    public void setMaxRepeats(int inputMaxRepeats) {
    //        if (inputMaxRepeats <= SYMBOL_MAX_REPEATS && inputMaxRepeats > 0) {
    //            this.inputMaxRepeats = inputMaxRepeats;
    //            System.out.println("""
    //                    Valid quantity of repeats
    //                    """);
    //        } else {
    //            throw new IllegalArgumentException("Quantity of max symbol repeats mustn't be higher than " + SYMBOL_MAX_REPEATS);
    //        }
    //    }

    public static boolean validatePassword(String password) {
        if (password.length() < getPASS_MAX_LENGTH() && password.length() > getPASS_MIN_LENGTH()) {
            return false;
        } else if (password.length() > getPASS_MAX_LENGTH()) {
            return false;
        }
        int maxRepeatedSymbols = 1; // текущая длина последовательности одинаковых символов
        char previousChar = password.charAt(0);
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == previousChar) {
                maxRepeatedSymbols++;
                if (maxRepeatedSymbols > getSYMBOL_MAX_REPEATS()) {
                    return false;
                }
            } else {
                maxRepeatedSymbols = 1;
            }
            previousChar = password.charAt(i);
        }
        return true;
    }
 */
}