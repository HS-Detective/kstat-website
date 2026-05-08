package net.dima.project.util;

public class Masking {
    public static String maskUserName(String name) {
        if (name == null || name.length() < 2) return name;

        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) {
            masked.append("*");
        }
        masked.append(name.charAt(name.length() - 1));

        return masked.toString();
    }
}
