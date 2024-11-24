package ru.yandex.practicum.ewm.request.service;

import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequestPrivate(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsPrivate(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
