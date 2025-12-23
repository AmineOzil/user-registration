package com.userapi.registration.aspect;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing log output.
 * Masks sensitive data (phone numbers) using regex pattern matching.
 */
final class SafeLog {

    private static final Pattern PHONE_PATTERN = Pattern.compile("phoneNumber='([^']+)'");

    /**
     * Converts method arguments to a safe loggable string.
     */
    static String toSafeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(toSafeValue(args[i]));
        }
        return sb.append("]").toString();
    }

    /**
     * Converts a value to a safe loggable string.
     * Automatically masks any phoneNumber field in the string representation.
     */
    static String toSafeValue(Object obj) {
        String str = String.valueOf(obj);
        return PHONE_PATTERN.matcher(str).replaceAll(match -> {
            String phone = match.group(1);
            String masked = maskPhone(phone);
            return "phoneNumber='" + masked + "'";
        });
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return phone.substring(0, 2) + "***" + phone.substring(phone.length() - 4);
    }

    private SafeLog() {
        // Utility class
    }
}