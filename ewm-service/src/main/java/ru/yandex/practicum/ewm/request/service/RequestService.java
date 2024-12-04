package ru.yandex.practicum.ewm.request.service;


import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancel(Long userId, Long requestId);
}