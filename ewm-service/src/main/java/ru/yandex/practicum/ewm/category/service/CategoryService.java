package ru.yandex.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;


public interface CategoryService {

    public CategoryDto createCategoryAdmin(NewCategoryDto newCategoryDto);

    public CategoryDto updateCategoryAdmin(CategoryDto categoryDto);

    public void deleteCategoryAdmin(Long categoryId);

    public List<CategoryDto> getCategories(Pageable pageable);

    public CategoryDto getCategory(Long categoryId);
}
