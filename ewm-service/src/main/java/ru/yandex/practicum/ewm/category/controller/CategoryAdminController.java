package ru.yandex.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    public static final String CAT_ID = "cat-id";
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    @DeleteMapping("/{" + CAT_ID + "}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(CAT_ID) Long catId) {
        categoryService.delete(catId);
    }

    @PatchMapping("/{" + CAT_ID + "}")
    public CategoryDto update(@PathVariable(CAT_ID) Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.update(catId, categoryDto);
    }
}