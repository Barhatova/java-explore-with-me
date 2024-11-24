package ru.yandex.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Email length can be 6 to 254")
    String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "Name can be 2 to 250")
    String name;
}