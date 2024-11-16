package ru.yandex.practicum.stats.validator;

import ru.yandex.practicum.dto.ParamDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetStatsValidator extends AbstractValidator {
    protected final ParamDto request;
    static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public GetStatsValidator(ParamDto request) {
        this.request = request;
    }

    public void validate() {
        if (request.getStartTime() == null || request.getStartTime().isBlank()) {
            validate.add("Не указано начало выгрузки статистики");
        }
        if (request.getEndTime() == null || request.getEndTime().isBlank()) {
            validate.add("Не указан конец выгрузки статистики");
        }
    }
}
