package ru.yandex.practicum.stats.validator;

import ru.yandex.practicum.stats.model.Validator;
import java.util.ArrayList;

abstract class AbstractValidator {
    protected Validator validateResult;

    public AbstractValidator() {
        this.validateResult = new Validator();
    }

    public ArrayList<String> getMessages() {
        return validateResult.getMessages();
    }

    public boolean isValid() {
        return validateResult.isValid();
    }
}
