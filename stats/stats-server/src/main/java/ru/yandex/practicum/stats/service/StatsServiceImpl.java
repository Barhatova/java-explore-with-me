package ru.yandex.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.stats.exception.BadRequestException;
import ru.yandex.practicum.stats.mapper.StatMapping;
import ru.yandex.practicum.stats.model.Stat;
import ru.yandex.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatMapping statMapper;

    @Override
    @Transactional
    public void createStat(ParamHitDto newStat) {
        log.info("StatsService: Beginning of method execution createStat().");

        log.info("StatsService.createStat(): Mapping from dto.");
        Stat paramHit = statMapper.toParamHit(newStat);

        log.info("StatsService.createStat(): Add endpoint hit to database.");
        statsRepository.save(paramHit);
        log.info("StatsService.createStat(): Stat saved successfully.");
    }

    @Override
    public List<StatDto> getStat(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris,
                                  boolean unique) {
        log.info("StatsService: Beginning of method execution getStat().");

        List<Stat> hits = new ArrayList<>();

        if (start.isAfter(end)) {
            throw new BadRequestException("The start date must be earlier than the end date.");
        }

        log.info("StatsService.getStat(): Checking for the existence of a uris list.");
        if (uris != null && !uris.isEmpty()) {
            log.info("StatsService.getStat(): Getting hits with uris list.");
            hits.addAll(statsRepository.getStatByUriForThePeriod(start, end, uris));
        } else {
            log.info("StatsService.getStat(): Getting hits without uris list.");
            hits.addAll(statsRepository.getStatByForThePeriod(start, end));
        }

        log.info("StatsService.getStat(): Checking for the existence of a unique parameter.");
        if (unique) {
            log.info("StatsService.getStat(): Getting hits with unique parameter.");
            Map<String, Stat> uniqueHitsByIp = new HashMap<>();

            for (Stat hit : hits) {
                String ip = hit.getIp();
                uniqueHitsByIp.putIfAbsent(ip, hit);
            }
            log.info("StatsService.getStat(): A list with unique hits was received successfully.");
            log.info("StatsService.getStat(): Collecting statistics based on a list uniqueHitsByIp.");
            return toViewStatsDtoList(uniqueHitsByIp.values());
        }

        log.info("StatsService.getStat(): Collecting statistics based on a list hits.");
        return toViewStatsDtoList(hits);
    }

    private List<StatDto> toViewStatsDtoList(Collection<Stat> hits) {
        log.info("StatsService: Beginning of method execution toViewStatsDtoList().");
        log.info("StatsService.toViewStatsDtoList(): Start collecting statistics.");
        Map<String, Map<String, Long>> groupedStats = hits.stream()
                .collect(Collectors.groupingBy(Stat::getApp,
                        Collectors.groupingBy(Stat::getUri, Collectors.counting())));

        List<StatDto> result = groupedStats.entrySet().stream()
                .flatMap(appEntry -> appEntry.getValue().entrySet().stream()
                        .map(uriEntry -> StatDto.builder()
                                .app(appEntry.getKey())
                                .uri(uriEntry.getKey())
                                .hits(uriEntry.getValue().longValue())
                                .build()))
                .sorted(Comparator.comparing(StatDto::getHits).reversed())
                .toList();
        log.info("StatsService.toViewStatsDtoList(): Statistics successfully collected.");
        return result;
    }
}