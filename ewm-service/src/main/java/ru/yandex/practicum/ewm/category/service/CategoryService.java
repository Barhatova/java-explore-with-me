package ru.yandex.practicum.ewm.category.service;

import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long id);

    CategoryDto update(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);
}