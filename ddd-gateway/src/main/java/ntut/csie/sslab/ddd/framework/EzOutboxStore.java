package ntut.csie.sslab.ddd.framework;

import ntut.csie.sslab.ddd.usecase.MessageData;
import ntut.csie.sslab.ddd.framework.ezes.PgMessageDbClient;
import ntut.csie.sslab.ddd.usecase.AggregateRootData;
import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EzOutboxStore<T extends OutboxData, ID> {
    private OrmClient<T, ID> ormClient;
    private PgMessageDbClient pgMessageDbClient;
    public EzOutboxStore(OrmClient<T, ID> ormClient,
                         PgMessageDbClient pgMessageDbClient) {
        this.ormClient = ormClient;
        this.pgMessageDbClient = pgMessageDbClient;
    }
    @Transactional
    public long save(T data) {
        long version = ormClient.saveAndUpdateVersion(data);
        pgMessageDbClient.saveDomainEventsWithoutVersion(
                new AggregateRootData(data.getStreamName(),
                        data.getVersion(), data.getDomainEventDatas()));
        return version;
    }

    public Optional<T> findById(ID id) {
        return ormClient.findById(id);
    }

    @Transactional
    public void delete(T data) {
        ormClient.deleteById((ID)data.getId());
        pgMessageDbClient.saveDomainEventsWithoutVersion(
                new AggregateRootData(data.getStreamName(),
                        data.getVersion(),
                        data.getDomainEventDatas()));
    }



    public List<DomainEventData> getCategoryEvent(String categoryName) {
        List<DomainEventData> domainEventData = new ArrayList<>();
        List<MessageData> messageDatas = pgMessageDbClient.findByEventType(categoryName);
        messageDatas.forEach(x -> {
            domainEventData.add(toDomainEventData(x));
        });
        return domainEventData;
    }

    private DomainEventData toDomainEventData(MessageData messageData) {
        return new DomainEventData(
                messageData.getId(),
                messageData.getEventType(),
                JSON_CONTENT_TYPE,
                messageData.getEventBody().getBytes(StandardCharsets.UTF_8),
                messageData.getMetadata().getBytes(StandardCharsets.UTF_8));
    }

    public static final String JSON_CONTENT_TYPE = "application/json";
}
