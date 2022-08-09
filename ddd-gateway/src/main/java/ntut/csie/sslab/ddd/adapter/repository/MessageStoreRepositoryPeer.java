package ntut.csie.sslab.ddd.adapter.repository;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MessageStoreRepositoryPeer {
    @Query(nativeQuery = true, value = "SELECT message_store.write_message(:id, :stream_name, :type, CAST(:data as JSONB), CAST(:metadata as JSONB), :expected_version)")
    long writeMessage(@Param("id") String id,
                      @Param("stream_name") String streamName,
                      @Param("type") String type,
                      @Param("data") String data,
                      @Param("metadata") String metadata,
                      @Param("expected_version") Long expectedVersion);

    @Query(nativeQuery = true, value = "SELECT message_store.write_message(:id, :stream_name, :type, CAST(:data as JSONB), CAST(:metadata as JSONB))")
    long writeMessage(@Param("id") String id,
                      @Param("stream_name") String streamName,
                      @Param("type") String type,
                      @Param("data") String data,
                      @Param("metadata") String metadata);

    @Query(nativeQuery = true, value = "select message_store.stream_version(CAST(:stream_name as VARCHAR))")
    long getStreamVersion(@Param("stream_name") String streamName);


    @Query(nativeQuery = true, value = "select message_store.message_store_version()")
    String getMessageStoreVersion();

    @Transactional
    default<ID, E extends DomainEvent> void saveDomainEvents(AggregateRoot<ID, E> aggregate, DomainEventTypeMapper domainEventTypeMapper){
        long expectedVersion = aggregate.getVersion() ;

        for(var event : aggregate.getDomainEvents()){
            expectedVersion = writeMessage(event.id().toString(),
                    aggregate.getStreamName(),
                    domainEventTypeMapper.toMappingType(event),
                    Json.asString(event),
                    "{}",
                    expectedVersion);
        }
    }

    @Transactional
    default<ID, E extends DomainEvent> void saveDomainEventsWithoutVersion(AggregateRoot<ID, E> aggregate, DomainEventTypeMapper domainEventTypeMapper){
        for(var event : aggregate.getDomainEvents()){
           writeMessage(event.id().toString(),
                    aggregate.getStreamName(),
                    domainEventTypeMapper.toMappingType(event),
                    Json.asString(event),
                    "{}");
        }
    }

    default long getExpectedVersion(AggregateRoot aggregateRoot) {
        return aggregateRoot.getVersion() + aggregateRoot.getDomainEventSize();
    }
}
