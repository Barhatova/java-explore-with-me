package ru.yandex.practicum.stats.validator;

import ru.yandex.practicum.stats.model.Validator;
import java.util.ArrayList;

abstract class AbstractValidator {
    protected Validator validate;

    public AbstractValidator() {
        this.validate = new Validator();
    }

    public ArrayList<String> getMessages() {
        return validate.getMessages();
    }

    public boolean isValid() {
        return validate.isValid();
    }
}
