package ru.yandex.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.client", "ru.yandex.practicum.ewm"})
public class EwmApplication {
    public static void main(String[] args) {
        SpringApplication.run(EwmApplication.class, args);
    }
}