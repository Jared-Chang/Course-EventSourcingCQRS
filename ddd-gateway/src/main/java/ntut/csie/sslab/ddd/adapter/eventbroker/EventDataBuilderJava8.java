package ntut.csie.sslab.ddd.adapter.eventbroker;

import com.eventstore.dbclient.EventData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.DomainEventData;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EventDataBuilderJava8 {

//    private static final JsonMapper mapper = new JsonMapper();
    private static final JsonMapper mapper = Json.mapper;

    private byte[] payload;
    private byte[] metadata;
    private String eventType;
    private boolean isJson;
    private UUID id;

    public static <A> EventDataBuilderJava8 json(String eventType, A payload) {
        EventDataBuilderJava8 self = new EventDataBuilderJava8();

        try {
            self.payload = mapper.writeValueAsBytes(payload);
            self.isJson = true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        self.eventType = eventType;

        return self;
    }

    public static EventDataBuilderJava8 binary(String eventType, byte[] payload) {
        EventDataBuilderJava8 self = new EventDataBuilderJava8();

        self.payload = payload;
        self.eventType = eventType;
        self.isJson = false;

        return self;
    }

    public EventDataBuilderJava8 eventId(UUID id) {
        this.id = id;
        return this;
    }

    public <A> EventDataBuilderJava8 metadataAsJson(A value) {
        try {
            this.metadata = mapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public EventDataBuilderJava8 metadataAsBytes(byte[] value) {
        this.metadata = value;
        return this;
    }

    public EventData build() {
        UUID eventId = this.id == null ? UUID.randomUUID() : this.id;
        String contentType = this.isJson ? "application/json" : "application/octet-stream";
        return new EventData(eventId, this.eventType, contentType, this.payload, this.metadata);
    }

    public DomainEventData buildNewDomainEventData() {
        UUID eventId = this.id == null ? UUID.randomUUID() : this.id;
        String contentType = this.isJson ? "application/json" : "application/octet-stream";
        byte[] userMetaData = this.metadata == null ? "{}".getBytes(StandardCharsets.UTF_8) : this.metadata;
        return new DomainEventData(eventId, this.eventType, contentType, this.payload, userMetaData);
    }

}
