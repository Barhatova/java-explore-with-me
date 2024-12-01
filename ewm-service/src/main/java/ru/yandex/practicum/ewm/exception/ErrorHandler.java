package ru.yandex.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidException(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> "Field: " + error.getField() + ". Error: " + error.getDefaultMessage() + ". Value: " + error.getRejectedValue())
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                String.join("; ", errors));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        String message = "Failed to convert value of type " + Objects.requireNonNull(e.getValue()).getClass().getSimpleName() +
                " to required type " + Objects.requireNonNull(e.getRequiredType()).getSimpleName() +
                "; nested exception is " + e.getCause().getMessage();
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                message
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "There are events associated with the category.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleViolationOfEditingRulesException(final ViolationOfEditingRulesException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEventParticipationConstraintException(final EventParticipationConstraintException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "Restriction of participation in the event.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintUpdatingException(final ConstraintUpdatingException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "Restriction of editing in the event.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                message
        );
    }
}