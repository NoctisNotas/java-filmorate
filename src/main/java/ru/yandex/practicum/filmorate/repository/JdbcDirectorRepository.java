package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public Collection<Director> findAll() {
        String sql = "SELECT * FROM directors ORDER BY director_id";
        return jdbcTemplate.query(sql, directorMapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sql, directorMapper, id);
            return Optional.ofNullable(director);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM directors WHERE director_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Director save(Director director) {

        String sql = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"director_id"});
            ps.setString(1,director.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        director.setId(id);

        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";

        int rowsUpdated = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Режиссёр с id=" + director.getId() + " не найден");
        }

        return findById(director.getId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql,id);
    }
}
