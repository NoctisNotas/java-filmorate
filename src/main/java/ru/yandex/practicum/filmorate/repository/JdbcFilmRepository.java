package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapperWithMpaAndGenre;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmRepository implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final GenreRepository genreRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final FilmMapperWithMpaAndGenre filmMapperWithMpaAndGenre;

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmMapper);
        films.forEach(this::loadFilmGenres);
        films.forEach(this::loadFilmDirectors);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
            if (film != null) {
                loadFilmGenres(film);
                loadFilmDirectors(film);
                loadMpaDetails(film);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        saveFilmGenres(film);
        saveFilmDirectors(film);
        return findById(filmId).orElseThrow();
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);
        updateFilmDirectors(film);
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        String sql = "SELECT f.* FROM films f " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmMapper, count);
        films.forEach(this::loadFilmGenres);
        films.forEach(this::loadFilmDirectors);
        return films;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortedByYear(long id) {
        String sql = "SELECT f.* FROM films AS f JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? ORDER BY f.release_date ASC;";
        List<Film> films = jdbcTemplate.query(sql, filmMapper, id);
        films.forEach(this::loadFilmGenres);
        films.forEach(this::loadFilmDirectors);
        return films;
    }

    public List<Film> getFilmsFromUsersThatLiked(List<Long> userId) {
        if (userId == null || userId.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_desc, " +
                "fg.genre_id, g.name AS genre_name, fd.director_id, d.name AS director_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.film_id IN (SELECT DISTINCT fl.film_id FROM film_likes AS fl WHERE fl.user_id IN (" +
                String.join(",", Collections.nCopies(userId.size(), "?")) +
                ")) ORDER BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, filmMapperWithMpaAndGenre, userId.toArray());
        return films;
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortedByLikes(long id) {
        String sql = "SELECT f.* FROM films AS f JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id WHERE fd.director_id = ? " +
                "GROUP BY f.film_id ORDER BY COUNT(fl.user_id) DESC;";
        List<Film> films = jdbcTemplate.query(sql, filmMapper, id);
        films.forEach(this::loadFilmGenres);
        films.forEach(this::loadFilmDirectors);
        return films;
    }

    public List<Long> getFilmsFromUser(Long id) {
        String sql = "SELECT film_id FROM film_likes " +
                "WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        String sql = "SELECT f.*, mr.name AS mpa_name, mr.description AS mpa_desc, " +
                "fg.genre_id, g.name AS genre_name, fd.director_id, d.name AS director_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "WHERE f.name ILIKE ? OR d.name ILIKE ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, filmMapperWithMpaAndGenre, "%" + query + "%", "%" + query + "%");
        return films;
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        String sql = "SELECT f.*, mr.name AS mpa_name, mr.description AS mpa_desc, " +
                "fg.genre_id, g.name AS genre_name, fd.director_id, d.name AS director_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "WHERE d.name ILIKE ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, filmMapperWithMpaAndGenre, "%" + query + "%");
        return films;
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        String sql = "SELECT f.*, mr.name AS mpa_name, mr.description AS mpa_desc, " +
                "fg.genre_id, g.name AS genre_name, fd.director_id, d.name AS director_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "WHERE f.name ILIKE ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, filmMapperWithMpaAndGenre, "%" + query + "%");
        return films;
    }

    @Override
    public List<Film> searchPopularFilms() {
        String sql = "SELECT f.*, mr.name AS mpa_name, mr.description AS mpa_desc, " +
                "fg.genre_id, g.name AS genre_name, fd.director_id, d.name AS director_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, filmMapperWithMpaAndGenre);
        return films;
    }

    private void loadFilmGenres(Film film) {
        String sql = "SELECT g.genre_id, g.name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.genre_id";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());

        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void loadFilmDirectors(Film film) {
        String sql = "SELECT d.director_id, d.name FROM film_directors fd JOIN directors d ON" +
                " fd.director_id = d.director_id WHERE fd.film_id = ? ORDER BY d.director_id";

        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));
            return director;
        }, film.getId());

        film.setDirectors(new LinkedHashSet<>(directors));

    }

    private void loadMpaDetails(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaRatingRepository.findById(film.getMpa().getId()).ifPresent(film::setMpa);
        }
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Genre> genresInOrder = new ArrayList<>(film.getGenres());
        Set<Long> genreIds = genresInOrder.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        String checkSql = "SELECT COUNT(*) FROM genres WHERE genre_id IN (" +
                genreIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";

        Integer foundCount = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                genreIds.toArray()
        );

        if (foundCount == null || foundCount != genreIds.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены");
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = genresInOrder.stream()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void saveFilmDirectors(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        List<Director> directorsInOrder = new ArrayList<>(film.getDirectors());
        Set<Long> directorIds = directorsInOrder.stream()
                .map(Director::getId)
                .collect(Collectors.toSet());

        String checkSql = "SELECT COUNT(*) FROM directors WHERE director_id IN (" +
                directorIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";

        Integer foundCount = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                directorIds.toArray()
        );

        if (foundCount == null || foundCount != directorIds.size()) {
            throw new NotFoundException("Один или несколько режиссеров не найдены");
        }

        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        List<Object[]> batchArgs = directorsInOrder.stream()
                .map(director -> new Object[]{film.getId(), director.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        saveFilmGenres(film);
    }

    private void updateFilmDirectors(Film film) {
        String deleteSql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        saveFilmDirectors(film);
    }
}