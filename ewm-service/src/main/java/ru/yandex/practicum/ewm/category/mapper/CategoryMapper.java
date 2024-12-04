package ru.yandex.practicum.ewm.category.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.model.Category;

@Component
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category fromNewCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}