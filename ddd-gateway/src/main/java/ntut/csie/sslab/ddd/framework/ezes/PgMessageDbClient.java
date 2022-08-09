package ntut.csie.sslab.ddd.framework.ezes;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.AggregateRootData;
import ntut.csie.sslab.ddd.usecase.MessageData;
import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public interface PgMessageDbClient extends JpaRepository<MessageData, String> {

    String EMPTY_JSON = String.valueOf(new JSONObject("{}"));
    String CHECKPOINT_STREAM_NAME_PREFIX = "$$Checkpoint-";

    String CHECKPOINT_EVENT = "$System$Checkpointed";
    String ALL_STREAM_NAME = "$all";

    default List<MessageData> findByStreamName(String streamName) {
        Long position = 0L;

        if (streamName.charAt(0) != '$') {
            return findByAggregateStreamName(streamName, position);
        }
        else if (streamName.substring(0, 4).equals("$et-")) {
            return findByEventType(streamName.substring(4));
        }
        else if (streamName.substring(0, 4).equals("$ce-")) {
            return findByCategory(streamName.substring(4) + "-%");
        }
        else if (streamName.substring(0, 4).equals("$all")) {
            return findAllStream(position);
        }
        else {
            throw new RuntimeException("Unsupported stream name: " + streamName);
        }
    }

    default PersistentConsumer subscribeToAll(String subscriberName, int pollingInterval){
        var streamName = CHECKPOINT_STREAM_NAME_PREFIX + subscriberName;
        if (getStreamVersion(streamName).isEmpty()){
            _writeMessage(UUID.randomUUID().toString(),
                    streamName,
                    CHECKPOINT_EVENT,
                    Json.asString(Checkpoint.valueOf(0)),
                    EMPTY_JSON);
        }

        return new PersistentConsumer(streamName, subscriberName, this, pollingInterval);
    }

    @Transactional
    @Modifying
    @Query(value = """
             UPDATE messages
             SET data = CAST(:checkpoint AS JSONB)
             WHERE stream_name = :stream_name              
             """, nativeQuery = true)
    int _ack(
            @Param("stream_name") String streamName,
            @Param("checkpoint") String checkpoint);


    default int ack(String streamName, Checkpoint checkpoint){
        requireNotNull("Stream Name", streamName);
        requireNotNull("Checkpoint", checkpoint);

        Optional<Long> maxVersion = getStreamVersion(streamName);
        if (maxVersion.isEmpty()){
            throw  new RuntimeException("Stream to be acked is not exist, stream name = " + streamName);
        }

        if (checkpoint.position() > maxVersion.get()){
            throw new RuntimeException("Checkpoint overflow, max position is " +
                    maxVersion.get() + " but checkpoint is " + checkpoint.position());
        }

        if (isSystemProjectionStream(streamName) && !isCheckpointStream(streamName)){
            throw  new RuntimeException("Cannot ack system stream " + streamName);
        }

        return _ack(streamName, Json.asString(checkpoint));
    }

    default boolean isSystemProjectionStream(String streamName){
        return streamName.substring(0, 1).equals("$");
    }

    default boolean isCheckpointStream(String streamName){
        return streamName.startsWith("$$");
    }

    @Query(nativeQuery = true, value = "SELECT * FROM message_store.get_last_stream_message(:stream_name)")
    Optional<MessageData> getLastStreamMessage(
            @Param("stream_name") String streamName);


    @Query(nativeQuery = true, value = "SELECT message_store.write_message(:id, :stream_name, :type, CAST(:data as JSONB), CAST(:metadata as JSONB), :expected_version)")
    long _writeMessage(@Param("id") String id,
                       @Param("stream_name") String streamName,
                       @Param("type") String type,
                       @Param("data") String data,
                       @Param("metadata") String metadata,
                       @Param("expected_version") Long expectedVersion);

    @Query(nativeQuery = true, value = "SELECT message_store.write_message(:id, :stream_name, :type, CAST(:data as JSONB), CAST(:metadata as JSONB))")
    long _writeMessage(@Param("id") String id,
                       @Param("stream_name") String streamName,
                       @Param("type") String type,
                       @Param("data") String data,
                       @Param("metadata") String metadata);

    @Query(nativeQuery = true, value = "select message_store.stream_version(CAST(:stream_name as VARCHAR))")
    Optional<Long> _getStreamVersion(@Param("stream_name") String streamName);

    @Query(nativeQuery = true, value = "select max(global_position) FROM messages")
    Optional<Long> _getAllStreamVersion();

    default Optional<Long> getStreamVersion(String streamName) {
        if (ALL_STREAM_NAME.equals(streamName) || streamName.startsWith(CHECKPOINT_STREAM_NAME_PREFIX)){
            return _getAllStreamVersion();
        }
        else {
            return _getStreamVersion(streamName);
        }
    }

    @Query(nativeQuery = true, value = "select message_store.message_store_version()")
    String getMessageStoreVersion();


    @Query(nativeQuery = true, value = "SELECT * FROM message_store.get_stream_messages(:stream_name, :position, :batch_size, NULL)")
    List<MessageData> _getStreamMessage(
            @Param("stream_name") String streamName,
            @Param("position") Long position,
            @Param("batch_size") Long batchSize);

    //    @Query(nativeQuery = true, value = "SELECT message_store.get_stream_messages(:stream_name, :position, :batch_size)")
//    List<String> getStreamMessage(
//            @Param("stream_name") String streamName,
//            @Param("position") Long position,
//            @Param("batch_size") Long batchSize,
//            @Param("condition") String condition);
    @Query(value = """
             SELECT id, CAST(data AS TEXT), type, stream_name, time, CAST(metadata AS TEXT), global_position, position 
             FROM messages AS m 
             WHERE m.stream_name = :stream_name
             AND position >= :position 
             ORDER BY m.global_position ASC
             """, nativeQuery = true)
    List<MessageData> findByAggregateStreamName(
            @Param("stream_name") String streamName,
            @Param("position") Long position);


    @Query(value = """
             SELECT id, CAST(data AS TEXT), type, stream_name, time, CAST(metadata AS TEXT), global_position, position 
             FROM messages AS m 
             WHERE m.type = :type 
             ORDER BY m.global_position ASC
             """, nativeQuery = true)
    List<MessageData> findByEventType(@Param("type") String eventType);

    @Query(value = """
             SELECT id, CAST(data AS TEXT), type, stream_name, time, CAST(metadata AS TEXT), global_position, position 
             FROM messages AS m 
             WHERE m.stream_name LIKE :category 
             ORDER BY m.global_position ASC
             """, nativeQuery = true)
    List<MessageData> findByCategory(@Param("category") String category);

    @Query(value = """
             SELECT id, CAST(data AS TEXT), type, stream_name, time, CAST(metadata AS TEXT), global_position, position 
             FROM messages AS m
             WHERE position >= :position 
             ORDER BY m.global_position ASC
             """, nativeQuery = true)
    List<MessageData> findAllStream(
            @Param("position") Long position);


    @Query(value = """
             SELECT id, CAST(data AS TEXT), type, stream_name, time, CAST(metadata AS TEXT), global_position, position
             FROM messages AS m
             WHERE m.stream_name = :streamName
                 AND m.type IN :types
             ORDER BY m.global_position ASC
             """, nativeQuery = true)
    List<MessageData> findByStreamNameAndEventTypeIn(@Param("streamName") String streamName, @Param("types") List<String> types);

    @Transactional
    default long saveDomainEvents(AggregateRootData aggregateRootData){
        long expectedVersion = aggregateRootData.getVersion() ;

        for(var event : aggregateRootData.getDomainEventDatas()){
            expectedVersion = _writeMessage(event.id().toString(),
                    aggregateRootData.getStreamName(),
                    event.eventType(),
                    new JSONObject(new String(event.eventData())).toString(),
                    String.valueOf(new JSONObject("{}")),
                    expectedVersion);
        }
        return expectedVersion;
    }

    @Transactional
    default void saveDomainEventsWithoutVersion(AggregateRootData aggregateRootData){

        for(var event : aggregateRootData.getDomainEventDatas()){
            _writeMessage(event.id().toString(),
                    aggregateRootData.getStreamName(),
                    event.eventType(),
                    new JSONObject(new String(event.eventData())).toString(),
                    String.valueOf(new JSONObject("{}")));
        }

    }


    default long getExpectedVersion(AggregateRoot aggregateRoot) {
        return aggregateRoot.getVersion() + aggregateRoot.getDomainEventSize();
    }
}
