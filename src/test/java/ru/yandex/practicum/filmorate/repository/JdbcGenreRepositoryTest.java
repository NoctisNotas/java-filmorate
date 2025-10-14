package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcGenreRepository.class, GenreMapper.class})
class JdbcGenreRepositoryTest {

    private final JdbcGenreRepository genreRepository;

    @Test
    void testFindAll() {
        Collection<Genre> genres = genreRepository.findAll();

        assertThat(genres).hasSize(6);
    }

    @Test
    void testFindById() {
        Optional<Genre> genre = genreRepository.findById(1L);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g -> {
                    assertThat(g.getId()).isEqualTo(1L);
                    assertThat(g.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    void testExistsById() {
        boolean exists = genreRepository.existsById(1L);

        assertThat(exists).isTrue();
    }
}