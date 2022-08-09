package ntut.csie.sslab.ezkanban.kanban.tag.usecase;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.RepositorySaveException;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class InMemoryTagRepository implements TagRepository {
    private final List<Tag> store = new ArrayList<>();
    private final DomainEventBus domainEventBus;

    public InMemoryTagRepository(DomainEventBus domainEventBus){
        this.domainEventBus = domainEventBus;
    }

    @Override
    public Optional<Tag> findById(String tagId) {
        Optional<Tag> tag = store.stream().filter(x -> x.getId().equals(tagId)).findAny();
        if (tag.isEmpty())
            return tag;

        var found = new Tag(
                tag.get().getBoardId(),
                tag.get().getId(),
                tag.get().getName(),
                tag.get().getColor());

        found.setVersion(tag.get().getVersion());
        return Optional.of(found);
    }

    @Override
    public void save(Tag tag) {
        requireNotNull("Tag", tag);

        var old = store.stream().filter(x -> x.getId().equals(tag.getId())).findAny();
        if (old.isPresent() && old.get().getVersion() != tag.getVersion()) {
            throw new RepositorySaveException(RepositorySaveException.OPTIMISTIC_LOCKING_FAILURE);
        }
        if (old.isPresent()) {
            store.removeIf(x -> x.getId().equals(tag.getId()));
        }
        tag.setVersion(tag.getVersion() + 1);
        store.add(tag);
        domainEventBus.postAll(tag);
    }

    @Override
    public void delete(Tag tag) {
        store.removeIf( x-> x.getId().equals(tag.getId()));
    }

    @Override
    public List<Tag> getTagsByBoardId(BoardId boardId) {
        return store.stream().filter(x -> x.getBoardId().equals(boardId)).toList();
    }
}
