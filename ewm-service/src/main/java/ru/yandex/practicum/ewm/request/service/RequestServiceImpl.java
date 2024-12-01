package ru.yandex.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.State;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.EventParticipationConstraintException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.repository.RequestRepository;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId) {
        log.info("Запрос на получение списка запросов");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequester(user);
        if (requests.isEmpty()) {
            return List.of();
        }
        List<ParticipationRequestDto> result = requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
        log.info("Список запросов получен");
        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("Запрос на создание запроса");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        if (event.getInitiator().equals(user)) {
            throw new EventParticipationConstraintException(String.format("Пользователь является создателем события", userId, eventId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventParticipationConstraintException(String.format("Событие не опубликовано" +
                    "Пользователь не может отправить запрос на участие в событии", userId, eventId));
        }
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new EventParticipationConstraintException(String.format("Количество заявок на участие в событии достигло предела ", eventId, event.getParticipantLimit()));
        }
        RequestStatus status = event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();
        request = requestRepository.save(request);
        if (status.equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        ParticipationRequestDto requestDto = requestMapper.toParticipationRequestDto(request);
        log.info("Запрос создан");
        return requestDto;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        log.info("Запрос на отмену запроса");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос не найден", requestId)));
        if (!request.getRequester().equals(user)) {
            throw new NotFoundException(String.format("Запрос не найден", requestId));
        }
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        ParticipationRequestDto requestDto = requestMapper.toParticipationRequestDto(request);
        log.info("Запрос отменен");
        return requestDto;
    }
}