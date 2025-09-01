package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;
    private Film film;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenNameIsNull() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название фильма не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        film.setName("   ");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название фильма не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        String longDescription = "A".repeat(201);
        film.setDescription(longDescription);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Описание не должно быть длиннее 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenDescriptionIsExactly200Chars() {
        String exactDescription = "A".repeat(200);
        film.setDescription(exactDescription);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenReleaseDateIsBefore1895() {
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenReleaseDateIsExactly1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldPassWhenReleaseDateIsAfter1895() {
        film.setReleaseDate(LocalDate.of(1896, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        film.setDuration(-10);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenDurationIsPositive() {
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldPassWhenReleaseDateIsNull() {
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "ReleaseDate может быть null");
    }

    @Test
    void shouldPassWhenDescriptionIsNull() {
        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Description может быть null");
    }
}