package ru.yandex.practicum.ewm.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.ObjectNotFoundException;
import ru.yandex.practicum.ewm.exception.RulesViolationException;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.Request;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.repository.RequestRepository;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequestPrivate(Long userId, Long eventId) {
        Request request = new Request();
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Событие по id {} не найдено", eventId)));
        User user = getUserOrThrow(userId);
        List<Request> requests = requestRepository.getAllByRequesterIdAndEventId(userId, eventId);
        if (!requests.isEmpty()) {
            throw new RulesViolationException("Ваш запрос добавлен");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new RulesViolationException("Владельцем события не может быть добавлен");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RulesViolationException("Не удается добавить в не опубликованное событие");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new RulesViolationException("Достигнут лимит участников");
        }
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
               return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsPrivate(Long userId) {
        getUserOrThrow(userId);
                return requestRepository.getAllByRequesterId(userId).stream()
                .map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUserOrThrow(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Запрос по id {} не найден", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
               return requestMapper.toDto(request);
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Пользователь по id {} не найден", id)));
    }
}