package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.GenericOutboxRepository;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxStore;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagOutboxRepository implements TagRepository {
    private final GenericOutboxRepository<Tag, TagData, String> outboxRepository;
    private final OutboxStore<TagData, String> store;

    public TagOutboxRepository(OutboxStore<TagData, String> store) {
        this.outboxRepository = new GenericOutboxRepository<>(store, new TagMapper());
        this.store = store;
    }

    @Override
    public Optional<Tag> findById(String tagId) {
        return outboxRepository.findById(tagId);
    }

    @Override
    public void save(Tag tag) {
        outboxRepository.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        outboxRepository.delete(tag);
    }

    @Override
    public List<Tag> getTagsByBoardId(BoardId boardId) {

        List<TagEvents.TagCreated> tagCreateds = store.getCategoryEvent(TagEvents.TypeMapper.TAG_CREATED)
                .stream().map(x -> (TagEvents.TagCreated) DomainEventMapper.toDomain(x)).filter(x-> x.boardId().equals(boardId)).toList();

        List<Tag> result = new ArrayList<>();
        for(var event : tagCreateds){
            Optional<TagData> tag = store.findById(event.tagId());
            if (tag.isPresent()){
                result.add(new TagMapper().toDomain(tag.get()));
            }
        }

        return result;
    }
}
