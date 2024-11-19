package ru.yandex.practicum.stats.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Validator {
    final ArrayList<String> messages;

    public Validator() {
        messages = new ArrayList<>();
    }

    public Validator(String message) {
        messages = new ArrayList<>();
        messages.add(message);
    }

    public boolean isValid() {
        return messages.isEmpty();
    }

    public void add(String message) {
        messages.add(message);
    }
}
