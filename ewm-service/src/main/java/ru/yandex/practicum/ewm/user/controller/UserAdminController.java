package ru.yandex.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        return userService.findAll(ids, from, size);
    }

    @DeleteMapping("/{user-id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("user-id") Long userId) {
        userService.deleteById(userId);
    }
}