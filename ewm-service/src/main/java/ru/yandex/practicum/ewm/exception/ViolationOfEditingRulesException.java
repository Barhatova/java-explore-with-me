package ru.yandex.practicum.ewm.exception;

public class ViolationOfEditingRulesException extends RuntimeException {
    public ViolationOfEditingRulesException(String message) {
        super(message);
    }
}
