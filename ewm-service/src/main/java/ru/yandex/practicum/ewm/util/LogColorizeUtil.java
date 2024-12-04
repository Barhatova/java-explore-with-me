package ru.yandex.practicum.ewm.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogColorizeUtil {
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RED = "\u001B[31m";

    public static String colorizeClass(String className) {
        return ANSI_BLUE + className + ANSI_RESET;
    }

    public static String colorizeMethod(String methodName) {
        return ANSI_GREEN + methodName + ANSI_RESET;
    }

    public static String colorizeError(String error) {
        return ANSI_RED + error + ANSI_RESET;
    }
}
