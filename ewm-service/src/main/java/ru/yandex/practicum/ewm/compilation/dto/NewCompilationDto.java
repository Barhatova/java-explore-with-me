package ru.yandex.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
    @UniqueElements
    List<Long> events;
    @Builder.Default
    boolean pinned = false;
}