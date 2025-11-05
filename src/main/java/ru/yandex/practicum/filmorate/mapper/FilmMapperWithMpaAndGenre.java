package ru.yandex.practicum.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class FilmMapperWithMpaAndGenre implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Film> films = new ArrayList<>();
        Long currentFilmId = null;
        Film currentFilm = null;

        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            if (currentFilmId == null || !currentFilmId.equals(filmId)) {
                if (currentFilm != null) {
                    films.add(currentFilm);
                }
                currentFilmId = filmId;
                currentFilm = new Film();

                currentFilm.setId(rs.getLong("film_id"));
                currentFilm.setName(rs.getString("name"));
                currentFilm.setDescription(rs.getString("description"));

                if (rs.getDate("release_date") != null) {
                    currentFilm.setReleaseDate(rs.getDate("release_date").toLocalDate());
                }

                currentFilm.setDuration(rs.getInt("duration"));

                MpaRating newMpa = new MpaRating();
                newMpa.setId(rs.getLong("mpa_id"));
                newMpa.setName(rs.getString("mpa_name"));
                newMpa.setDescription(rs.getString("mpa_desc"));
                currentFilm.setMpa(newMpa);

                currentFilm.setGenres(new LinkedHashSet<>());
                currentFilm.setDirectors(new LinkedHashSet<>());
            }

            Long genreId = rs.getLong("genre_id");
            if (!rs.wasNull() && genreId != 0) {
                Genre newGenre = new Genre();
                newGenre.setId(genreId);
                newGenre.setName(rs.getString("genre_name"));
                boolean genreExists = currentFilm.getGenres().stream()
                        .anyMatch(genre -> genre.getId().equals(genreId));
                if (!genreExists) {
                    currentFilm.getGenres().add(newGenre);
                }
            }

            Long directorId = rs.getLong("director_id");
            if (!rs.wasNull() && directorId != 0) {
                Director director = new Director();
                director.setId(directorId);
                director.setName(rs.getString("director_name"));
                boolean directorExists = currentFilm.getDirectors().stream()
                        .anyMatch(dir -> dir.getId().equals(directorId));
                if (!directorExists) {
                    currentFilm.getDirectors().add(director);
                }
            }
        }

        if (currentFilm != null) {
            films.add(currentFilm);
        }
        return films;
    }

}
