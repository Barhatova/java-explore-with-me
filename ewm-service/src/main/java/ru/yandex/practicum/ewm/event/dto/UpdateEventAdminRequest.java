package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.event.model.Location;
import ru.yandex.practicum.ewm.event.model.StateAdmin;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    StateAdmin stateAction;
    @Size(min = 3, max = 120)
    String title;
}