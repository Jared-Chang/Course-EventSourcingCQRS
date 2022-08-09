package ntut.csie.sslab.ddd.usecase;

import java.io.Closeable;
import java.util.List;
import java.util.Optional;

public interface EventStore extends Closeable {
    void save(AggregateRootData aggregateRootData);

    Optional<AggregateRootData> load(String aggregateStreamName);

    List<DomainEventData> getCategoryEvent(String categoryName);

    default String getStreamName(String category, String id) {
        return category + "-" + id;
    }

    void close();


    List<DomainEventData> getEventFromStream(String streamName, long revision);
    Optional<DomainEventData> getLastEventFromStream(String streamName);
}
