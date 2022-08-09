package ntut.csie.sslab.ddd.adapter.repository;

import ntut.csie.sslab.ddd.usecase.MessageData;
import ntut.csie.sslab.ddd.framework.ezes.PgMessageDbClient;
import ntut.csie.sslab.ddd.usecase.AggregateRootData;
import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.EventStore;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EzesStoreAdapter implements EventStore {
    private PgMessageDbClient client;
    public static final String JSON_CONTENT_TYPE = "application/json";

    public EzesStoreAdapter(PgMessageDbClient client) {
        this.client = client;
    }

    @Override
    public void save(AggregateRootData aggregateRootData) {
        if (null == aggregateRootData) {
            throw new RuntimeException("AggregateData cannot be null.");
        }

        long version = client.saveDomainEvents(aggregateRootData);
        aggregateRootData.setVersion(version);
    }

    @Override
    public Optional<AggregateRootData> load(String aggregateStreamName) {
        if (null == aggregateStreamName) {
            throw new IllegalArgumentException("AggregateStreamName cannot be null.");
        }

        List<MessageData> messageDatas = client.findByStreamName(aggregateStreamName);

        if (messageDatas.isEmpty())
            return Optional.empty();

        AggregateRootData aggregateRootData = new AggregateRootData();
        messageDatas.stream().forEach(x -> {
            aggregateRootData.getDomainEventDatas().add(toDomainEventData(x));
        });
        aggregateRootData.setVersion(messageDatas.get(messageDatas.size() - 1).getPosition());
        aggregateRootData.setStreamName(aggregateStreamName);

        return Optional.of(aggregateRootData);
    }

    @Override
    public List<DomainEventData> getCategoryEvent(String categoryName) {
        List<DomainEventData> domainEventData = new ArrayList<>();
        List<MessageData> messageDatas = client.findByEventType(categoryName);

        messageDatas.forEach(x -> {
            domainEventData.add(toDomainEventData(x));
        });

        return domainEventData;
    }

    @Override
    public void close() {
        //do nothing
    }

    @Override
    public List<DomainEventData> getEventFromStream(String streamName, long revision) {
        return null;
    }

    @Override
    public Optional<DomainEventData> getLastEventFromStream(String streamName) {
        return Optional.empty();
    }

    private DomainEventData toDomainEventData(MessageData messageData) {
        return new DomainEventData(
                messageData.getId(),
                messageData.getEventType(),
                JSON_CONTENT_TYPE,
                messageData.getEventBody().getBytes(StandardCharsets.UTF_8),
                messageData.getMetadata().getBytes(StandardCharsets.UTF_8));
    }
}
