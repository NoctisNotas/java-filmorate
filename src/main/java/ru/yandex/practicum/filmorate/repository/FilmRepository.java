package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    
    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Film save(Film film);

    Film update(Film film);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> findPopularFilms(int count);

    boolean existsById(Long id);

    Collection<Film> findFilmsByDirectorSortedByLikes(long id);

    Collection<Film> findFilmsByDirectorSortedByYear(long id);
    
    List<Film> getFilmsFromUsersThatLiked(List<Long> id);

    List<Long> getFilmsFromUser(Long id);
}
