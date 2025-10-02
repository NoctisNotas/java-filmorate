package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.Collection;
import java.util.Optional;

public interface MpaRatingRepository {
    Collection<MpaRating> findAll();
    Optional<MpaRating> findById(Long id);
    boolean existsById(Long id);
}