package ru.yandex.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
