package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRatingRepository implements MpaRatingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingMapper mpaRatingMapper;

    @Override
    public Collection<MpaRating> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, mpaRatingMapper);
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        try {
            MpaRating mpa = jdbcTemplate.queryForObject(sql, mpaRatingMapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE mpa_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}