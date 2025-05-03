package org.example.authservice.utils;

public class Generate {
    public static String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
