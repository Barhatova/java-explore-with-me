package ru.yandex.practicum.ewm.exception;

public class RulesViolationException extends RuntimeException {
    public RulesViolationException(String message) {
        super(message);
    }
}
