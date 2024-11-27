package ru.yandex.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.stats.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stat,Long> {

    @Query("SELECT s FROM Stat s "
            + "WHERE s.uri IN (:uris) "
            + "AND timestamp between :start AND :end ")
    List<Stat> getStatByUriForThePeriod(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT s FROM Stat s "
            + "WHERE timestamp between :start AND :end ")
    List<Stat> getStatByForThePeriod(LocalDateTime start, LocalDateTime end);
}
