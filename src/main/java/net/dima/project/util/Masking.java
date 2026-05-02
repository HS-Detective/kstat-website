package net.dima.project.util;

public class Masking {
    public static String maskUserName(String name) {
        if (name == null || name.length() < 2) return name;

        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        return name.charAt(0) + "*" + name.charAt(name.length() - 1);
    }
}
