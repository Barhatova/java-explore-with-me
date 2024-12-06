package ru.yandex.practicum.ewm.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 2000)
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;
    @Column(nullable = false, length = 7000)
    String description;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;
    @Embedded
    Location location;
    @Column(nullable = false)
    boolean paid;
    @Builder.Default
    @Column(name = "participant_limit", nullable = false)
    Long participantLimit = 0L;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Builder.Default
    @Column(name = "request_moderation", nullable = false)
    boolean requestModeration = true;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    State state;
    @Column(nullable = false, length = 120)
    String title;
    Long views;
    Double rating;
}