package ru.yandex.practicum.ewm.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.repository.CategoryRepository;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.ewm.exception.ObjectNotFoundException;
import ru.yandex.practicum.ewm.exception.RulesViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {
    final CategoryRepository categoryRepository;
    final CategoryMapper categoryMapper;
    final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategoryAdmin(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ObjectAlreadyExistException("Категория с названием {} уже существует");
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryAdmin(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Категория по id {} не найдена", categoryDto.getId())));
        if (!existingCategory.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new ObjectAlreadyExistException("Категория с названием {} уже существует");
        }
        existingCategory.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(existingCategory));

    }

    @Override
    @Transactional
    public void deleteCategoryAdmin(Long categoryId) {
        getCategoryOrThrow(categoryId);
        if (eventRepository.getAllByCategoryId(categoryId) != null) {
            throw new RulesViolationException(
                    String.format("Категория по id не может быть удалена, потому что есть события", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        List<CategoryDto> allCategories = categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        return allCategories;
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Категория по id {} не найдена", categoryId)));
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    private Category getCategoryOrThrow(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Категория по id {} не найдена", id)));
    }
}
