package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapperWithMpaAndGenre;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcFilmRepository.class,
        JdbcMpaRatingRepository.class,
        JdbcGenreRepository.class,
        FilmMapper.class,
        FilmMapperWithMpaAndGenre.class,
        GenreMapper.class,
        MpaRatingMapper.class})
class JdbcFilmRepositoryTest {

    private final JdbcFilmRepository filmRepository;
    private final JdbcTemplate jdbcTemplate;

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
    void testSaveFilm() {

        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");

        Film newFilm = new Film();
        newFilm.setName("New Test Film");
        newFilm.setDescription("Test Description");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(150);

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
        assertThat(savedFilm.getId()).isEqualTo(1L);
        assertThat(savedFilm.getName()).isEqualTo("New Test Film");
        assertThat(savedFilm.getDescription()).isEqualTo("Test Description");
        assertThat(savedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(savedFilm.getDuration()).isEqualTo(150);
        assertThat(savedFilm.getMpa()).isNotNull();
        assertThat(savedFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(savedFilm.getGenres().iterator().next().getId()).isEqualTo(1L);
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

    @Test
    void testDeleteFilm() {
        filmRepository.deleteById(1L);

        Optional<Film> filmAfterDelete = filmRepository.findById(1L);
        assertThat(filmAfterDelete).isEmpty();

        boolean exists = filmRepository.existsById(1L);
        assertThat(exists).isFalse();
    }
    void testGetFilmsFromUsersThatLiked() {
        List<Long> users1 = new ArrayList<>();
        users1.add(1L);
        users1.add(3L);
        List<Film> films1 = filmRepository.getFilmsFromUsersThatLiked(users1);

        assertThat(films1.size()).isEqualTo(3);

        List<Long> users2 = new ArrayList<>();
        List<Film> films2 = filmRepository.getFilmsFromUsersThatLiked(users2);
        assertThat(films2.size()).isZero();
    }

    @Test
    void testGetFilmsFromUser() {
        List<Long> films = filmRepository.getFilmsFromUser(1L);

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.contains(1L)).isTrue();
        assertThat(films.contains(2L)).isTrue();
    }

    @Test
    void testGetCommonFilms() {
        Collection<Film> commonFilms = filmRepository.getCommonFilms(1, 2);

        assertThat(commonFilms).hasSize(1);
        assertThat(commonFilms.iterator().next().getId()).isEqualTo(1L);
    }
}