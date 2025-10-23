package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FeedService feedService;

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        review = reviewRepository.save(review);
        feedService.addFeedEvent(review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }

    public Review update(Review review) {
        if (!reviewRepository.findById(review.getReviewId()).isPresent()) {
            throw new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден");
        }
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        review = reviewRepository.update(review);
        feedService.addFeedEvent(review.getUserId(), "REVIEW", "UPDATE", review.getReviewId());
        return review;
    }

    public void delete(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
        Review review = optionalReview.get();
        reviewRepository.deleteById(id);
        feedService.addFeedEvent(review.getUserId(), "REVIEW", "REMOVE", id);
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    public List<Review> getReviews(Long filmId, int count) {
        if (filmId == null) {
            return reviewRepository.findAll(count);
        }
        if (!filmRepository.existsById(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return reviewRepository.findByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    private void validateReviewAndUser(Long reviewId, Long userId) {
        if (!reviewRepository.findById(reviewId).isPresent()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}