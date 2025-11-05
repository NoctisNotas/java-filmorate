package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedMapper implements RowMapper<FeedEvent> {

    @Override
    public FeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedEvent feedEvent = new FeedEvent();
        feedEvent.setEventId(rs.getLong("event_id"));

        if (rs.getTimestamp("event_date") != null) {
            feedEvent.setTimestamp(rs.getTimestamp("event_date").toInstant().toEpochMilli());
        }

        feedEvent.setUserId(rs.getLong("user_id"));

        String eventType = rs.getString("event_type");
        try {
            feedEvent.setEventType(EventType.valueOf(eventType));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Вид события " + eventType + " не найден");
        }

        String operation = rs.getString("operation");
        try {
            feedEvent.setOperation(OperationType.valueOf(operation));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Вид операции " + operation + " не найден");
        }

        feedEvent.setEntityId(rs.getLong("entity_id"));

        return feedEvent;
    }
}
