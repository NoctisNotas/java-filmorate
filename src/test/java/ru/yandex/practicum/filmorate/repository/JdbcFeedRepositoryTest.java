package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcFeedRepository.class, FeedMapper.class, JdbcUserRepository.class, UserMapper.class
,JdbcFilmRepository.class, FilmMapper.class})
class JdbcFeedRepositoryTest {

    private final JdbcFeedRepository feedRepository;
    private final JdbcUserRepository userRepository;

    @Test
    void testAddedWhenFriendAdded() {
        userRepository.addFriend(2L, 1L);

        List<FeedEvent> feedEvents = feedRepository.getFeedEvents(1L);

        assertThat(feedEvents.get(0).getUserId()).isEqualTo(2L);
        assertThat(feedEvents.get(0).getEntityId()).isEqualTo(1L);
        assertThat(feedEvents.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(feedEvents.get(0).getOperation()).isEqualTo(OperationType.ADD);
    }
}
