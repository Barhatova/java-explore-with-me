package ru.yandex.practicum.ewm.utill;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.client.StatsClient;

@Configuration
public class AppConfig {
    @Bean
    StatsClient statsClient() {
        RestTemplate restTemplate = new RestTemplate();
        return new StatsClient(restTemplate);
    }
}