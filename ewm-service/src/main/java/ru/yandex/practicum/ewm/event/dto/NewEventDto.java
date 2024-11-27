package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotNull
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotNull
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    LocalDateTime eventDate;
    @NotNull
    Location location;
    @Builder.Default
    boolean paid = false;
    @Builder.Default
    @PositiveOrZero
    long participantLimit = 0L;
    @Builder.Default
    boolean requestModeration = true;
    @Size(min = 3, max = 120)
    String title;
}