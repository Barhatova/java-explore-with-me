package ru.yandex.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.repository.CategoryRepository;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.mapper.EventMapper;
import ru.yandex.practicum.ewm.event.model.*;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.*;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.repository.RequestRepository;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.ewm.event.specification.EventSpecification.getAdminFilters;
import static ru.yandex.practicum.ewm.event.specification.EventSpecification.getPublicFilters;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    private String serviceId;

    @Override
    @Transactional
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         SortType sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {
        log.info("Запрос на получение событий");
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("Конец не может быть раньше начала");
            }
        }
        Specification<Event> spec = getPublicFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        Sort sorting = Sort.by("eventDate");
        if (sort != null) {
            if (sort == SortType.EVENT_DATE) {
                sorting = Sort.by("eventDate");
            } else if (sort == SortType.VIEWS) {
                sorting = Sort.by("views");
            }
        }
        Pageable pageable = PageRequest.of(from / size, size, sorting);
        Page<Event> events = eventRepository.findAll(spec, pageable);
        sendStatisticalData(request);
        List<Event> eventList = events.getContent().stream()
                .peek(event -> {
                    Long views = getUniqueViews(event, request.getRequestURI());
                    views++;
                    event.setViews(views);
                })
                .toList();
        eventRepository.saveAll(eventList);
        List<EventShortDto> shortEvents = events.getContent().stream()
                .map(eventMapper::toEventShortDtoFromEvent)
                .toList();
        log.info("События получены");
        return shortEvents;
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        log.info("Запрос на получение события по идентификатору");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Событие не найдено", eventId));
        }
        Long views = getUniqueViews(event, request.getRequestURI());
        views++;
        event.setViews(views);
        event = eventRepository.save(event);
        EventFullDto fullDto = eventMapper.toEventFullDtoFromEvent(event);
        sendStatisticalData(request);
        log.info("События по идентификатору получено");
        return fullDto;
    }

    @Override
    public List<EventFullDto> getFullEvents(List<Long> users,
                                            List<State> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Integer from,
                                            Integer size) {
        log.info("Запрос на получение события");
        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> spec = getAdminFilters(users, states, categories, rangeStart, rangeEnd);
        Page<Event> events = eventRepository.findAll(spec, pageable);
        List<EventFullDto> eventFullDtos = events.getContent().stream()
                .map(eventMapper::toEventFullDtoFromEvent)
                .toList();
        log.info("События получено");
        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Запрос на изменение события админом");Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        StringBuilder updatedFieldsLog = new StringBuilder();
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
            updatedFieldsLog.append("Annotation|");
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Категория не найдена", updateEventAdminRequest.getCategory()))));
            updatedFieldsLog.append("Category|");
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
            updatedFieldsLog.append("Description|");
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
            updatedFieldsLog.append("Location|");
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
            updatedFieldsLog.append("Paid|");
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
            updatedFieldsLog.append("ParticipantLimit|");
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
            updatedFieldsLog.append("RequestModeration|");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateAdmin.PUBLISH_EVENT) {
                if (event.getState() == State.PENDING) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ConstraintUpdatingException("Событие может быть опубликовано только в том случае, если оно находится в состоянии ожидания");
                }
            } else if (updateEventAdminRequest.getStateAction() == StateAdmin.REJECT_EVENT) {
                if (event.getState() == State.PENDING) {
                    event.setState(State.CANCELED);
                } else {
                    throw new ConstraintUpdatingException("Событие может быть отклонено только в том случае, если оно еще не было опубликовано");
                }
            }
            updatedFieldsLog.append("StateAction|");
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime minDateConstraint = LocalDateTime.now().plusHours(2);
            if (event.getPublishedOn() != null && !isStartDateValid(event.getPublishedOn(), updateEventAdminRequest.getEventDate(), 1)) {
                throw new ConstraintUpdatingException("Дата начала изменяемого мероприятия должна быть назначена не ранее, чем через час с момента публикации");
            } else if (updateEventAdminRequest.getEventDate().isBefore(minDateConstraint)) {
                throw new BadRequestException(String.format("Должно содержать дату не ранее %s. " +
                        "Value: %s", minDateConstraint, updateEventAdminRequest.getEventDate()));
            }
            event.setEventDate(updateEventAdminRequest.getEventDate());
            updatedFieldsLog.append("EventDate|");
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }
        String updatedFields = updatedFieldsLog.toString().replaceAll("\\|$", "").replace("|", ", ");
        event = eventRepository.save(event);
        EventFullDto fullDto = eventMapper.toEventFullDtoFromEvent(event);
        log.info("Событие изменено админом");
        return fullDto;
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(Long userId, int from, int size) {
        log.info("Запрос на получение события текущего пользователя");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> page = eventRepository.findAllByInitiator(user, pageable);
        if (!page.hasContent()) {
            log.info("Не найдены события пользователя");
            return List.of();
        }
        List<EventShortDto> events = page.getContent().stream()
                .map(eventMapper::toEventShortDtoFromEvent)
                .toList();
        log.info("События текущего пользователя получены");
        return events;
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        log.info("Запрос на создание события");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория не найдена", newEventDto.getCategory())));
        LocalDateTime minDateConstraint = LocalDateTime.now().plusHours(2);
        if (newEventDto.getEventDate().isBefore(minDateConstraint)) {
            throw new BadRequestException(String.format("Должно содержать дату не ранее %s. " +
                    "Value: %s", minDateConstraint, newEventDto.getEventDate()));
        }
        Event event = eventMapper.toEventFromNewEventDto(newEventDto, category, user);
        event = eventRepository.save(event);
        EventFullDto fullDto = eventMapper.toEventFullDtoFromEvent(event);
        log.info("Событие создано");
        return fullDto;
    }

    @Override
    public EventFullDto getFullEventByIdForCurrentUser(Long userId, Long eventId) {
        log.info("Запрос на получение события текущим пользователем по идентификатору");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользоваель не найден", userId)));
        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        EventFullDto fullDto = eventMapper.toEventFullDtoFromEvent(event);
        log.info("Событие текущим пользователем по идентификатору получено");
        return fullDto;
    }


    @Override
    @Transactional
    public EventFullDto updateByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Запрос на обновление текущим пользователем");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ViolationOfEditingRulesException("Можно изменить только отложенные или отмененные события");
        }
        StringBuilder updatedFieldsLog = new StringBuilder();
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
            updatedFieldsLog.append("Annotation|");
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Категория не найдена", updateEventUserRequest.getCategory()))));
            updatedFieldsLog.append("Category|");
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
            updatedFieldsLog.append("Description|");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (!isStartDateValid(LocalDateTime.now(), updateEventUserRequest.getEventDate(), 2)) {
                throw new BadRequestException("Дата и время, на которые запланировано мероприятие, не могут быть ранее, чем через два часа после текущего момента");
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
            updatedFieldsLog.append("EventDate|");
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
            updatedFieldsLog.append("Location|");
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
            updatedFieldsLog.append("Paid|");
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
            updatedFieldsLog.append("ParticipantLimit|");
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
            updatedFieldsLog.append("RequestModeration|");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateUser.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }
            if (updateEventUserRequest.getStateAction().equals(StateUser.CANCEL_REVIEW) && event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
            }
            updatedFieldsLog.append("StateAction|");
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }
        String updatedFields = updatedFieldsLog.toString().replaceAll("\\|$", "").replace("|", ", ");
        event = eventRepository.save(event);
        EventFullDto fullDto = eventMapper.toEventFullDtoFromEvent(event);
        log.info("Обновлено текущим пользователем");
        return fullDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId) {
        log.info("Запрос на получение запросов текущим пользователем");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        Collection<ParticipationRequest> requests = requestRepository.findAllByEvent(event);
        if (requests.isEmpty()) {
            log.info("Запросы не найдены");
            return List.of();
        }
        List<ParticipationRequestDto> requestDtos = requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
        log.info("Запросы текущего пользователя получены");
        return requestDtos;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Запрос на обновление статуса");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь не найден", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено", eventId)));
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(requestRepository.findAllByEvent(event).stream().map(requestMapper::toParticipationRequestDto).toList())
                    .rejectedRequests(List.of())
                    .build();
        }
        Collection<ParticipationRequest> pendingRequests = requestRepository.findAllByEventAndStatus(event, RequestStatus.PENDING);
        if (pendingRequests.isEmpty()) {
            throw new ViolationOfEditingRulesException("Статус запроса должен быть PENDING");
        }
        long confirmedRequestsCount = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);
        if (statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED) && confirmedRequestsCount >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников заполнен");
        }
        for (ParticipationRequest request : pendingRequests) {
            if (confirmedRequestsCount < event.getParticipantLimit()) {
                request.setStatus(statusUpdateRequest.getStatus());
                requestRepository.save(request);
                if (statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                    confirmedRequestsCount++;
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
        }
        event.setConfirmedRequests(confirmedRequestsCount);
        eventRepository.save(event);
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).stream().map(requestMapper::toParticipationRequestDto).toList())
                .rejectedRequests(requestRepository.findAllByEventAndStatus(event, RequestStatus.REJECTED).stream().map(requestMapper::toParticipationRequestDto).toList())
                .build();
        log.info("Статус обновлен");
        return result;
    }

    private boolean isStartDateValid(LocalDateTime publicationDate, LocalDateTime startDate, int constraint) {
        long hoursBetween = ChronoUnit.HOURS.between(publicationDate, startDate);
        return hoursBetween >= constraint;
    }

    private void sendStatisticalData(HttpServletRequest request) {
        ParamHitDto stat = ParamHitDto.builder()
                .app(serviceId)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.create(stat);
    }

    private List<StatDto> convertResponseToList(ResponseEntity<Object> response) {
        if (response.getBody() == null) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(response.getBody(), new TypeReference<List<StatDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Не удалось преобразовать ответ в список", e);
        }
    }

    private Long getUniqueViews(Event event, String uri) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String startDate = event.getCreatedOn().format(formatter);
        String endDate = LocalDateTime.now().format(formatter);
        List<String> uris = List.of(uri);

        List<StatDto> stats = convertResponseToList(statsClient.getStats(startDate, endDate, uris, true));

        return stats.isEmpty()
                ? 0L
                : stats.stream().mapToLong(StatDto::getHits).sum();
    }
}