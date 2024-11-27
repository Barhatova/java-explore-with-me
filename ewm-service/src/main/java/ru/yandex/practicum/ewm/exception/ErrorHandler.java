package ru.yandex.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Incorrectly made request.");
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("Integrity constraint has been violated.");
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("The required object was not found.");
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRulesViolationException(final RulesViolationException e) {
        log.warn("For the requested operation the conditions are not met.");
        return new ErrorResponse(HttpStatus.FORBIDDEN, "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(final AlreadyExistException e) {
        log.warn("Object with already exist");
        return new ErrorResponse(HttpStatus.CONFLICT, "Object with already exist",
                e.getMessage());

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestException(final InvalidRequestException e) {
        log.warn("Incorrectly made request.");
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalServerError(final Throwable e) {
        log.warn("Internal server error: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Missing request parameter", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.warn("BadRequestException", e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }
}
