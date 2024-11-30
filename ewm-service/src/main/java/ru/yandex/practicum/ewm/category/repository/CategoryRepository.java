package ru.yandex.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public boolean existsByName(String name);

}
