package ru.yandex.practicum.ewm.request.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;

@Component
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .event(participationRequest.getEvent().getId())
                .build();
    }
}