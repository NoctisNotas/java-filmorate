package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FeedService.class, JdbcFeedRepository.class, FeedMapper.class,
        UserService.class, JdbcUserRepository.class, UserMapper.class,
        FilmService.class, JdbcFilmRepository.class, FilmMapper.class,
        GenreService.class, JdbcGenreRepository.class, GenreMapper.class,
        MpaRatingService.class, JdbcMpaRatingRepository.class, MpaRatingMapper.class,
        ReviewService.class, JdbcReviewRepository.class, ReviewMapper.class})
class JdbcFeedRepositoryTest {

    private final JdbcFeedRepository feedRepository;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewService reviewService;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testEventAddedWhenFriendAddedAndRemoved() {
        userService.addFriend(2L, 1L);
        userService.deleteFriend(2L, 1L);

        List<FeedEvent> feedEvents = feedRepository.getFeedEvents(2L);

        assertThat(feedEvents.get(0).getUserId()).isEqualTo(2L);
        assertThat(feedEvents.get(0).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(feedEvents.get(0).getOperation()).isEqualTo(OperationType.ADD);

        assertThat(feedEvents.get(1).getUserId()).isEqualTo(2L);
        assertThat(feedEvents.get(1).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(1).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(feedEvents.get(1).getOperation()).isEqualTo(OperationType.REMOVE);
    }

    @Test
    void testEventAddedWhenLikeAddedAndRemoved() {
        filmService.addLike(4L, 4L);
        filmService.removeLike(4L, 4L);

        List<FeedEvent> feedEvents = feedRepository.getFeedEvents(4L);

        assertThat(feedEvents.get(0).getUserId()).isEqualTo(4L);
        assertThat(feedEvents.get(0).getEntityId()).isEqualTo(4L);
        assertThat(feedEvents.get(0).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(feedEvents.get(0).getOperation()).isEqualTo(OperationType.ADD);

        assertThat(feedEvents.get(1).getUserId()).isEqualTo(4L);
        assertThat(feedEvents.get(1).getEntityId()).isEqualTo(4L);
        assertThat(feedEvents.get(1).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(feedEvents.get(1).getOperation()).isEqualTo(OperationType.REMOVE);
    }

    @Test
    void testEventAddedWhenReviewAddedUpdatedAndRemoved() {

        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.execute("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");

        Review review = new Review();
        review.setContent("Тестовый отзыв");
        review.setIsPositive(true);
        review.setUserId(4L);
        review.setFilmId(4L);

        reviewService.create(review);
        review.setContent("Тестовый отзыв измененный");
        reviewService.update(review);
        reviewService.delete(review.getReviewId());

        List<FeedEvent> feedEvents = feedRepository.getFeedEvents(4L);

        assertThat(feedEvents.get(0).getUserId()).isEqualTo(4L);
        assertThat(feedEvents.get(0).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(0).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feedEvents.get(0).getOperation()).isEqualTo(OperationType.ADD);

        assertThat(feedEvents.get(1).getUserId()).isEqualTo(4L);
        assertThat(feedEvents.get(1).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(1).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feedEvents.get(1).getOperation()).isEqualTo(OperationType.UPDATE);

        assertThat(feedEvents.get(2).getUserId()).isEqualTo(4L);
        assertThat(feedEvents.get(2).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(2).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feedEvents.get(2).getOperation()).isEqualTo(OperationType.REMOVE);
    }
}