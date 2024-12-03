package ru.yandex.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.ewm.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c JOIN FETCH c.events WHERE c.pinned = :pinned")
    Page<Compilation> findAllByPinned(boolean pinned, Pageable pageable);

    @Query("SELECT c FROM Compilation c JOIN FETCH c.events")
    Page<Compilation> findAll(Pageable pageable);
}