package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.AggregateRootData;
import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class SnapshottedTagEventSourcingRepository implements TagRepository {

    private final EventStore eventStore;
    private final TagEventSourcingRepository tagEventSourcingRepository;

    private int snapshotIncrement = 5;

    public SnapshottedTagEventSourcingRepository(TagEventSourcingRepository tagEventSourcingRepository, EventStore eventStore) {
        requireNotNull("TagEventSourcingRepository", tagEventSourcingRepository);
        requireNotNull("EventSourcingStore", eventStore);

        this.tagEventSourcingRepository = tagEventSourcingRepository;
        this.eventStore = eventStore;
    }

    @Override
    public Optional<Tag> findById(String tagId) {

        Optional<DomainEventData> domainEventData =
                eventStore.getLastEventFromStream(getSnapshottedStreamName(tagId));

        if (domainEventData.isEmpty()){
            return tagEventSourcingRepository.findById(tagId);
        }

        DomainEvent.Snapshotted snapshotted = DomainEventMapper.toDomain(domainEventData.get());
        Tag tag = Tag.fromSnapshot(Json.readAs(snapshotted.snapshot().getBytes(), Tag.TagSnapshot.class));
        var events = DomainEventMapper.toDomain(
                eventStore.getEventFromStream(tag.getStreamName(), tag.getVersion()+1));
        events.forEach( x -> tag.apply(x));
        return Optional.of(tag);
    }


    @Override
    public void save(Tag tag) {

        tagEventSourcingRepository.save(tag);

        Optional<DomainEventData> snapshotEventData =
                eventStore.getLastEventFromStream(getSnapshottedStreamName(tag.getId()));
        if (snapshotEventData.isPresent()){
            DomainEvent.Snapshotted snapshotted = DomainEventMapper.toDomain(snapshotEventData.get());
            if (tag.getVersion() - snapshotted.version() >= snapshotIncrement){
                saveSnapshot(tag);
            }
        }
        else if (tag.getVersion() >= snapshotIncrement){
            saveSnapshot(tag);
        }
    }

    private void saveSnapshot (Tag tag){
        Tag.TagSnapshot snapshot = tag.getSnapshot();
        AggregateRootData data = new AggregateRootData();
        data.setVersion(-1);
        data.setStreamName(getSnapshottedStreamName(tag.getId()));
        var snapshotted = new DomainEvent.Snapshotted(tag.getId(), tag.getCategory(), Json.asString(snapshot), tag.getVersion(), UUID.randomUUID(), DateProvider.now());
        data.setDomainEventDatas(Arrays.asList(DomainEventMapper.toData(snapshotted)));
        eventStore.save(data);
        return;
    }

    private String getSnapshottedStreamName(String tagId){
        return "Snapshot-Tag-" + tagId;
    }

    @Override
    public void delete(Tag tag) {
        tagEventSourcingRepository.delete(tag);
    }

    @Override
    public List<Tag> getTagsByBoardId(BoardId boardId) {
        return tagEventSourcingRepository.getTagsByBoardId(boardId);
    }
}
