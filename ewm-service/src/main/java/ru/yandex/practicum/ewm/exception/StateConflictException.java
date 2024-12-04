package ru.yandex.practicum.ewm.exception;

public class StateConflictException extends RuntimeException {
    public StateConflictException(String message) {
        super(message);
    }
}
