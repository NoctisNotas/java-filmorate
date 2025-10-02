package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcMpaRatingRepository.class, MpaRatingMapper.class})
class JdbcMpaRatingRepositoryTest {

    private final JdbcMpaRatingRepository mpaRatingRepository;

    @Test
    void testFindAll() {
        Collection<MpaRating> ratings = mpaRatingRepository.findAll();

        assertThat(ratings).hasSize(5);
    }

    @Test
    void testFindById() {
        Optional<MpaRating> rating = mpaRatingRepository.findById(1L);

        assertThat(rating)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(1L);
                    assertThat(mpa.getName()).isEqualTo("G");
                });
    }

    @Test
    void testExistsById() {
        boolean exists = mpaRatingRepository.existsById(1L);

        assertThat(exists).isTrue();
    }
}