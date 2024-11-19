package ru.yandex.practicum.stats.validator;

import ru.yandex.practicum.dto.ParamDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetStatsValidator extends AbstractValidator {

    static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected final ParamDto request;

    public GetStatsValidator(ParamDto request) {
        this.request = request;
    }

    public void validate() {
        if (request.getStartTime() == null || request.getStartTime().isBlank()) {
            validateResult.add("Не указано время начала выгрузки статистики");
        }
        if (request.getEndTime() == null || request.getEndTime().isBlank()) {
            validateResult.add("Не указано время конца выгрузки статистики");
        }
    }
}
