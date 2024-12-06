package ru.yandex.practicum.ewm.rating.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingRequestDto {
    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    double rating;
    @Size(min = 20, max = 5000)
    String comment;
}