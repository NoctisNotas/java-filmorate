package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class JdbcFeedRepository implements FeedRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FeedMapper feedMapper;

    @Override
    public FeedEvent addFeedEvent(Long userId, String eventType, String operation, Long entityId) {
        String sql = "INSERT INTO feed (event_date, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        Instant now = Instant.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"event_id"});
            ps.setTimestamp(1, Timestamp.from(now));
            ps.setLong(2, userId);
            ps.setString(3, eventType);
            ps.setString(4, operation);
            ps.setLong(5, entityId);
            return ps;
        }, keyHolder);

        Long eventId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        FeedEvent feedEvent = new FeedEvent();
        feedEvent.setEventId(eventId);
        feedEvent.setTimestamp(now.toEpochMilli());

        try {
            feedEvent.setEventType(EventType.valueOf(eventType));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Вид события " + eventType + " не найден");
        }

        try {
            feedEvent.setOperation(OperationType.valueOf(operation));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Вид операции " + operation + " не найден");
        }

        feedEvent.setEntityId(entityId);

        return feedEvent;
    }

    @Override
    public List<FeedEvent> getFeedEvents(Long userId) {
        String sql = "SELECT f.* FROM feed f " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sql, feedMapper, userId);
    }
}
