package org.com.tools;

import org.com.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
    /*
        Принцип одностороннего хеширования:
        исходный пароль преобразуется математической функцией в строку фиксированной длины (хеш).
        Восстановить исходный текст из хеша невозможно.

        При регистрации:
        сервис принимает сырой пароль,
        вычисляет его хеш и сохраняет в сущность User именно строку хеша.

        При входе (login):
        сервис принимает введённый пароль,
        хеширует его тем же алгоритмом и сравнивает полученный хеш с тем,
        который сохранён у пользователя в базе.
     */

    // хэширование пароля для регистрации
    public static String hashPassword(String rawPassword) {
        // Для получения надежного цифрового отпечатка в виде строки из шестнадцатеричных символов (SHA-256)
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : encodedHash){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexStringBuilder.append('0');
                hexStringBuilder.append(hex);
            }
            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // хэширование пароля для аутентификации
    public static boolean hashMatchesPassword(String rawPassword, String storedPasswordHash) {
//        return hashPassword(rawPassword).equals(storedPasswordHash);
        String rawHash = hashPassword(rawPassword);
        byte[] rawPassBytes = rawHash.getBytes(StandardCharsets.UTF_8);
        byte[] storedPassBytes = storedPasswordHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(rawPassBytes, storedPassBytes);
    }
}
