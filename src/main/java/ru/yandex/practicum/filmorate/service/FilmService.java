package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final DirectorRepository directorRepository;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;
    private final FeedService feedService;
    private final DirectorService directorService;

    public Collection<Film> getAll() {
        return filmRepository.findAll();
    }

    public Film getFilm(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film create(Film film) {
        validateMpaRating(film);
        validateGenres(film);
        validateDirectors(film);
        return filmRepository.save(film);
    }

    public Film update(Film film) {
        if (!filmRepository.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        validateMpaRating(film);
        validateGenres(film);
        validateDirectors(film);
        return filmRepository.update(film);
    }

    public void addLike(Long id, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id: " + userId + " не существует");
        }
        feedService.addFeedEvent(userId, "LIKE", "ADD", id);
        filmRepository.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id: " + userId + " не существует");
        }
        filmRepository.removeLike(id, userId);
        feedService.addFeedEvent(userId, "LIKE", "REMOVE", id);
    }

    public Collection<Film> getPopularFilms(int count, Long genreId, Integer year) {
        return filmRepository.findPopularFilms(count, genreId, year);
    }

    public Collection<Film> getCommonFilms(long userId, long friendId) {
        return filmRepository.getCommonFilms(userId, friendId);
    }

    public Collection<Film> findFilmsByDirector(Long id, String sortBy) {
        if (!directorRepository.existsById(id)) {
            throw new NotFoundException("Режиссер с таким id: " + id + " не существует");
        }
        if ("year".equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortedByYear(id);
        } else if ("likes".equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortedByLikes(id);
        } else {
            throw new IllegalArgumentException("Неверный параметр sortBy: " + sortBy);
        }
    }

    public List<Film> searchFilms(String query, String by) {
        log.info("Search films - query: '{}', by: '{}'", query, by);

        if (query == null || query.isBlank()) {
            log.info("Empty query, returning popular films");
            List<Film> popularFilms = new ArrayList<>(filmRepository.searchPopularFilms());
            popularFilms.sort(Comparator.comparing(Film::getId).reversed());
            return popularFilms;
        }

        String searchQuery = query.toLowerCase().trim();
        log.info("Processed search query: '{}'", searchQuery);

        List<Film> result;

        if (by == null || by.isBlank()) {
            log.info("Searching by title only");
            result = filmRepository.searchFilmsByTitle(searchQuery);
        } else {
            String[] filters = by.split(",");
            List<String> filtersList = Arrays.asList(filters);
            log.info("Search filters: {}", filtersList);

            if (filtersList.contains("director") && filtersList.contains("title")) {
                log.info("Searching by director AND title");
                result = filmRepository.searchFilmsByDirectorAndTitle(searchQuery);
            } else if (filtersList.contains("director")) {
                log.info("Searching by director only");
                result = filmRepository.searchFilmsByDirector(searchQuery);
            } else if (filtersList.contains("title")) {
                log.info("Searching by title only");
                result = filmRepository.searchFilmsByTitle(searchQuery);
            } else {
                log.info("No valid filters, searching by title");
                result = filmRepository.searchFilmsByTitle(searchQuery);
            }
        }

        result.sort(Comparator.comparing(Film::getId).reversed());
        log.info("Found {} films for query '{}'", result.size(), query);

        return result;
    }

    private void validateMpaRating(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("MPA rating обязателен для фильма");
        }
        mpaRatingService.getMpaRating(film.getMpa().getId());
    }

    private void validateGenres(Film film) {
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreService.getGenre(genre.getId()));
        }
    }

    public void deleteFilm(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        filmRepository.deleteById(id);
    }

    private void validateDirectors(Film film) {
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> directorService.getDirector(director.getId()));
        }
    }
}