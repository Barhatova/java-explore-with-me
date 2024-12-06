package ru.yandex.practicum.ewm.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;
    Double rating;
}