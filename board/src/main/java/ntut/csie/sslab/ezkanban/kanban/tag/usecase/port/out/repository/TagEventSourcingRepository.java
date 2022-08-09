package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ddd.usecase.GenericEventSourcingRepository;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagEventSourcingRepository implements TagRepository {

    private final GenericEventSourcingRepository<Tag> eventSourcingRepository;
    private final EventStore eventStore;

    public TagEventSourcingRepository(EventStore eventStore) {
        this.eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, Tag.class, Tag.CATEGORY);
        this.eventStore = eventStore;
    }

    @Override
    public Optional<Tag> findById(String tagId) {
        return eventSourcingRepository.findById(tagId);
    }

    @Override
    public void save(Tag tag) {
        eventSourcingRepository.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        eventSourcingRepository.delete(tag);
    }

    @Override
    public List<Tag> getTagsByBoardId(BoardId boardId) {
        List<TagEvents.TagCreated> tagCreateds = eventStore.getCategoryEvent(TagEvents.TypeMapper.TAG_CREATED)
                .stream().map(x -> (TagEvents.TagCreated) DomainEventMapper.toDomain(x)).
                filter( x-> x.boardId().equals(boardId)).toList();

        List<Tag> result = new ArrayList<>();
        for(var event : tagCreateds){
            Optional<Tag> tag = eventSourcingRepository.findById(event.tagId());
            if (tag.isPresent()){
                result.add(tag.get());
            }
        }
        return result;
    }
}
