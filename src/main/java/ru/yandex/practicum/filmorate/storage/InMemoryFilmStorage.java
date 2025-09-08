package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        log.info("Получение всех фильмов, найдено: {}", films.size());
        return films.values();
    }

    @Override
    public Film getFilm(Long id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film.getId());

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм создан успешно, ID: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма: {}", film.getId());

        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления: фильм с id = {} не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        films.put(film.getId(), film);
        log.info("Фильм обновлен успешно, ID: {}", film.getId());
        return film;
    }

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
