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
        if (request.getTimestamp() == null || request.getTimestamp().isBlank()) {
            validate.add("Не указаны дата и время совершения запроса к эндпоинту");
        }
        if (request.getUri() == null || request.getUri().isBlank()) {
            validate.add("Не указан URI для запроса");
        }
        if (request.getApp() == null || request.getApp().isBlank()) {
            validate.add("Не указан идентификатор сервиса для записи информации");
        }
        if (request.getIp() == null || request.getIp().isBlank()) {
            validate.add("Не указан IP-адрес автора запроса");
        }
    }
}
