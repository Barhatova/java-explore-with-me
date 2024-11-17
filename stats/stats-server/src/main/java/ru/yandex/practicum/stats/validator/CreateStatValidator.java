package ru.yandex.practicum.stats.validator;

import ru.yandex.practicum.dto.ParamHitDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateStatValidator extends AbstractValidator {
    protected final ParamHitDto request;

    public CreateStatValidator(ParamHitDto request) {
        this.request = request;
    }

    public void validate() {
        if (request.getApp() == null || request.getApp().isBlank()) {
            validateResult.add("Не указан идентификатор сервиса");
        }
        if (request.getUri() == null || request.getUri().isBlank()) {
            validateResult.add("Не указан URI");
        }
        if (request.getIp() == null || request.getIp().isBlank()) {
            validateResult.add("Не указан IP-адрес автора");
        }
        if (request.getTimestamp() == null || request.getTimestamp().isBlank()) {
            validateResult.add("Не указаны дата и время совершения запроса");
        }
    }
}
