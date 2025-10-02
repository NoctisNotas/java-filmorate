package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcFilmRepository.class,
        JdbcMpaRatingRepository.class,
        JdbcGenreRepository.class,
        FilmMapper.class,
        GenreMapper.class,
        MpaRatingMapper.class})
class JdbcFilmRepositoryTest {

    private final JdbcFilmRepository filmRepository;

    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = filmRepository.findById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("Film One");
                    assertThat(film.getDescription()).isEqualTo("Description for film one");
                    assertThat(film.getGenres()).hasSize(2);
                });
    }

    @Test
    void testFindAllFilms() {
        Collection<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(4);
    }

    @Test
    void testSaveFilm() {
        Film newFilm = new Film();
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.of(2020, 6, 6));
        newFilm.setDuration(100);

        MpaRating mpa = new MpaRating();
        mpa.setId(1L);
        newFilm.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(1L);
        genres.add(genre);
        newFilm.setGenres(genres);

        Film savedFilm = filmRepository.save(newFilm);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo("New Film");
        assertThat(savedFilm.getGenres()).hasSize(1);
    }

    @Test
    void testUpdateFilm() {
        Optional<Film> existingFilm = filmRepository.findById(1L);
        assertThat(existingFilm).isPresent();

        Film filmToUpdate = existingFilm.get();
        filmToUpdate.setName("Updated Film Name");
        filmToUpdate.setDescription("Updated Description");

        Film updatedFilm = filmRepository.update(filmToUpdate);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testFindPopularFilms() {
        Collection<Film> popularFilms = filmRepository.findPopularFilms(2);

        assertThat(popularFilms).hasSize(2);
        assertThat(popularFilms.iterator().next().getId()).isEqualTo(3L);
    }

    @Test
    void testAddAndRemoveLike() {
        filmRepository.addLike(1L, 3L);

        Collection<Film> popularFilms = filmRepository.findPopularFilms(10);
        boolean film1Found = popularFilms.stream()
                .anyMatch(film -> film.getId().equals(1L));
        assertThat(film1Found).isTrue();

        filmRepository.removeLike(1L, 3L);
    }

    @Test
    void testExistsById() {
        assertThat(filmRepository.existsById(1L)).isTrue();
        assertThat(filmRepository.existsById(999L)).isFalse();
    }

    @Test
    void testFilmGenresAreLoaded() {
        Optional<Film> film = filmRepository.findById(1L);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getGenres()).hasSize(2);
                    assertThat(f.getGenres())
                            .extracting(Genre::getId)
                            .containsExactlyInAnyOrder(1L, 2L);
                });
    }
}