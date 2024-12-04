package ru.yandex.practicum.ewm.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "requester_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDateTime created;
    @ManyToOne
    @JoinColumn(nullable = false)
    Event event;
    @ManyToOne
    @JoinColumn(nullable = false)
    User requester;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RequestStatus status;
}