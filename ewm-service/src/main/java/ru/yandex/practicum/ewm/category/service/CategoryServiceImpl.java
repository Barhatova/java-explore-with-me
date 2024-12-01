package ru.yandex.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.repository.CategoryRepository;
import ru.yandex.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.info("Запрос на создание категории");
        Category category = categoryMapper.fromNewCategoryDto(newCategoryDto);
        category = categoryRepository.save(category);
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        log.info("Категория создана");
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        log.info("Запрос на удаление категории");
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Каегория не найдена", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория удалена");
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        log.info("Запрос на изменение категории");
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория не найдена", catId)));
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        CategoryDto categoryDtoUpdated = categoryMapper.toCategoryDto(category);
        log.info("Категория изменена");
        return categoryDtoUpdated;
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Запрос на получение списка категорий");
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        if (categoryPage.isEmpty()) {
            log.info("Категорий нет");
            return List.of();
        }

        List<CategoryDto> categories = categoryPage.stream()
                .map(categoryMapper::toCategoryDto)
                .toList();

        log.info("Список категорий получен");
        return categories;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.info("Запрос на получение категории по идентификатору");
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория не найдена", catId)));
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        log.info("Категория по идентификатору получена");
        return categoryDto;
    }
}