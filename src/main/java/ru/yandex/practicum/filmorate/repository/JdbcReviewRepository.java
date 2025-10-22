package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    @Override
    public Review save(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    @Override
    public Optional<Review> findById(Long id) {
        String sql = getReviewWithUsefulSql() + " WHERE r.review_id = ? GROUP BY r.review_id";

        try {
            Review review = jdbcTemplate.queryForObject(sql, reviewMapper, id);
            return Optional.ofNullable(review);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Review> findByFilmId(Long filmId, int count) {
        String sql = "SELECT r.*, " +
                "COUNT(CASE WHEN rl.is_like = true THEN 1 END) as likes_count, " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END) as dislikes_count, " +
                "(COUNT(CASE WHEN rl.is_like = true THEN 1 END) - " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END)) as useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, reviewMapper, filmId, count);
    }

    @Override
    public List<Review> findAll(int count) {
        String sql = "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, " +
                "COUNT(CASE WHEN rl.is_like = true THEN 1 END) as likes_count, " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END) as dislikes_count, " +
                "(COUNT(CASE WHEN rl.is_like = true THEN 1 END) - " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END)) as useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id " +
                "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, reviewMapper, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String deleteSql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId);

        String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
        jdbcTemplate.update(insertSql, reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String deleteSql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId);

        String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
        jdbcTemplate.update(insertSql, reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private String getReviewWithUsefulSql() {
        return "SELECT r.*, " +
                "COUNT(CASE WHEN rl.is_like = true THEN 1 END) as likes_count, " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END) as dislikes_count, " +
                "(COUNT(CASE WHEN rl.is_like = true THEN 1 END) - " +
                "COUNT(CASE WHEN rl.is_like = false THEN 1 END)) as useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.review_id = rl.review_id";
    }
}