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
    private final StatMapping statMapping;

    @Override
    @Transactional
    public void createStat(ParamHitDto stat) {
        Stat endpointHit = statMapping.toParamHit(stat);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<StatDto> groupStat(List<Stat> stats, boolean unique) {
        List<StatDto> statForOutput = new ArrayList<>();
        Map<String, List<Stat>> groupStat = stats.stream()
                .collect(Collectors.groupingBy(Stat::getUri));

        for (Map.Entry<String, List<Stat>> stat : groupStat.entrySet()) {
            String key = stat.getKey();
            long count = stat.getValue().size();
            Stat curreentStat = stat.getValue().getFirst();
            if (unique) {
                Set<String> uniqueIp =  stat.getValue().stream()
                        .map(Stat::getIp)
                        .collect(Collectors.toSet());
                count = uniqueIp.size();
            }
            StatDto statDto = StatDto.builder()
                    .app(curreentStat.getApp())
                    .uri(key)
                    .hits(count)
                    .build();
            statForOutput.add(statDto);
        }
        return statForOutput.stream()
                .sorted(Comparator.comparing(StatDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<StatDto> getStat(LocalDateTime start,
                                 LocalDateTime end,
                                 List<String> uris,
                                 boolean unique) {
        List<Stat> hits = new ArrayList<>();

        if (start.isAfter(end)) {
            throw new BadRequestException("Дата начала должна быть раньше даты окончания");
        }
        if (uris != null && !uris.isEmpty()) {
            hits.addAll(statsRepository.findByTimestampBetweenAndUriIn(start, end, uris));
        } else {
            hits.addAll(statsRepository.findByTimestampBetween(start, end));
        }

        if (unique) {
            Map<String, Stat> uniqueHitsByIp = new HashMap<>();

            for (Stat hit : hits) {
                String ip = hit.getIp();
                uniqueHitsByIp.putIfAbsent(ip, hit);
            }
            return toViewStatsDtoList(uniqueHitsByIp.values());
        }
        return toViewStatsDtoList(hits);
    }

    private List<StatDto> toViewStatsDtoList(Collection<Stat> hits) {
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
        return result;
    }
}