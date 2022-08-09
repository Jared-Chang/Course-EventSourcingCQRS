package ntut.csie.sslab.ddd.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.entity.common.Json;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteDomainEventTest {

    @Test
    public void should_succeed_when_write_remote_domain_event() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        Date now = DateProvider.now();

        BoardCreated boardCreated = new BoardCreated(id, "ScrumBoard", now);
        RemoteDomainEvent remoteBoardCreated = new RemoteDomainEvent(boardCreated, "DDDCORE", now);

        Map<String, String> map = Json.readValue(remoteBoardCreated.getJsonEvent(), Map.class);
        Instant.parse(map.get("occurredOn"));

        assertEquals("BoardCreated", remoteBoardCreated.getEventSimpleName());
        assertEquals("ntut.csie.sslab.ddd.entity.RemoteDomainEventTest$BoardCreated", remoteBoardCreated.getEventType());
        assertEquals("DDDCORE", remoteBoardCreated.getTag());
        assertEquals(id.toString(), map.get("id"));
        assertEquals(now.toInstant(), Instant.parse(map.get("occurredOn")));
        assertEquals("ScrumBoard", map.get("boardName"));

        assertEquals(Json.asString(boardCreated), remoteBoardCreated.getJsonEvent());
    }


    record BoardCreated(
            UUID id,
            String boardName,
            Date occurredOn
    ) implements DomainEvent {
        @Override
        public String aggregateId() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

}