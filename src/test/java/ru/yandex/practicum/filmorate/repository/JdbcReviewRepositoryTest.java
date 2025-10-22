package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcReviewRepository.class, ReviewMapper.class})
class JdbcReviewRepositoryTest {

    private final JdbcReviewRepository reviewRepository;

    @Test
    void testFindById() {
        Optional<Review> review = reviewRepository.findById(1L);

        assertThat(review).isPresent();
        assertThat(review.get().getContent()).isEqualTo("Отличный фильм");
        assertThat(review.get().getUserId()).isEqualTo(1L);
        assertThat(review.get().getFilmId()).isEqualTo(1L);
        assertThat(review.get().getIsPositive()).isTrue();
    }

    @Test
    void testFindByFilmId() {
        List<Review> reviews = reviewRepository.findByFilmId(1L, 10);

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getFilmId()).isEqualTo(1L);
        assertThat(reviews.get(0).getReviewId()).isEqualTo(1L);
        assertThat(reviews.get(1).getReviewId()).isEqualTo(2L);
    }

    @Test
    void testFindAll() {
        List<Review> reviews = reviewRepository.findAll(10);

        assertThat(reviews).hasSize(3);

        assertThat(reviews)
                .extracting(Review::getReviewId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void testUpdateReview() {
        Optional<Review> existingReview = reviewRepository.findById(1L);
        assertThat(existingReview).isPresent();

        Review reviewToUpdate = existingReview.get();
        reviewToUpdate.setContent("Обновленный отзыв");
        reviewToUpdate.setIsPositive(false);

        Review updatedReview = reviewRepository.update(reviewToUpdate);

        assertThat(updatedReview.getContent()).isEqualTo("Обновленный отзыв");
        assertThat(updatedReview.getIsPositive()).isFalse();

        Optional<Review> retrievedReview = reviewRepository.findById(1L);
        assertThat(retrievedReview).isPresent();
        assertThat(retrievedReview.get().getContent()).isEqualTo("Обновленный отзыв");
    }

    @Test
    void testDeleteReview() {
        Optional<Review> review = reviewRepository.findById(3L);
        assertThat(review).isPresent();

        reviewRepository.deleteById(3L);

        Optional<Review> deletedReview = reviewRepository.findById(3L);
        assertThat(deletedReview).isEmpty();
    }

    @Test
    void testAddLike() {
        reviewRepository.addLike(3L, 4L);

        Optional<Review> review = reviewRepository.findById(3L);
        assertThat(review).isPresent();
        assertThat(review.get().getUseful()).isEqualTo(1);
    }

    @Test
    void testAddDislike() {
        reviewRepository.addDislike(3L, 4L);

        Optional<Review> review = reviewRepository.findById(3L);
        assertThat(review).isPresent();
        assertThat(review.get().getUseful()).isEqualTo(-1);
    }
}