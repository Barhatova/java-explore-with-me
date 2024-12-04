package ru.yandex.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;

@RestController
@RequestMapping("admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    public static final String COMP_ID = "comp-id";
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{" + COMP_ID + "}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable(COMP_ID) Long compId) {
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{" + COMP_ID + "}")
    public CompilationDto update(@PathVariable(COMP_ID) Long compId,
                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }
}