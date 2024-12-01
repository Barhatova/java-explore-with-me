package ru.yandex.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.stats.model.Stat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stat, Integer> {

    Collection<Stat> getStatByUriForThePeriod(LocalDateTime start, LocalDateTime end, List<String> uris);

    Collection<Stat> getStatByForThePeriod(LocalDateTime start, LocalDateTime end);
}