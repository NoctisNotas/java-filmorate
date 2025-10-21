package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorRepository {
    Collection<Director> findAll();

    Optional<Director> findById(Long id);

    boolean existsById(Long id);

    Director save(Director director);

    Director update(Director director);

    void delete(Long id);

}
