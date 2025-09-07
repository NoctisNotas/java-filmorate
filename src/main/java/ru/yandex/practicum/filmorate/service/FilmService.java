package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(Long id, Long userId) {
        log.debug("Добавление лайка фильму {} от пользователя {}", id, userId);
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь уже поставил лайк этому фильму");
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        if (user == null) {
            log.warn("Пользователя с таким id: {} не существует", userId);
            throw new NotFoundException("Пользователя с таким id: " + userId + " не существует");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Пользователь {} поставил лайк фильму {}. ", userId, film.getName());
    }

    public void removeLike(Long id, Long userId) {
        log.debug("Удаление лайка фильму {} от пользователя {}", id, userId);
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователя с таким id: {} не существует", userId);
            throw new NotFoundException("Пользователя с таким id: " + userId + " не существует");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Пользователь {} удалил лайк с фильма {}.", userId, film.getName());
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Запрос {} популярных фильмов", count);
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
