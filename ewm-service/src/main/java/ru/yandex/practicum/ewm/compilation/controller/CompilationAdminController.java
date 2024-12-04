package ru.yandex.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;

@RestController
@RequestMapping("admin/compilations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationAdminController {
    final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{comp-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("comp-id") Long compId) {
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{comp-id}")
    public CompilationDto update(@PathVariable("comp-id") Long compId,
                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }
}