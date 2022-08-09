package ntut.csie.sslab.ezkanban.kanban.tag.entity;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.AggregateSnapshot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Tag extends AggregateRoot<String, DomainEvent> implements AggregateSnapshot<Tag.TagSnapshot> {

    public final static String CATEGORY = "Tag";
    public record TagSnapshot(BoardId boardId, String tagId, String name, String color, AtomicLong version){}

    private Tag(){}

    public static Tag fromSnapshot(TagSnapshot snapshot){
        Tag tag = new Tag();
        tag.setSnapshot(snapshot);
        return tag;
    }

    @Override
    public TagSnapshot getSnapshot() {
        return new TagSnapshot(boardId, id, name, color, version);
    }

    @Override
    public void setSnapshot(TagSnapshot snapshot) {
        this.boardId = snapshot.boardId;
        this.id = snapshot.tagId;
        this.name = snapshot.name;
        this.color = snapshot.color;
        this.version = snapshot.version;
    }


    private BoardId boardId;
    private String name;
    private String color;

    public Tag(BoardId boardId, String tagId, String name, String color) {
        super(tagId);
        this.boardId = boardId;
        this.name = name;
        this.color = color;
        this.isDeleted = false;
        this.addDomainEvent(new TagEvents.TagCreated(boardId,
                                                    tagId,
                                                    name,
                                                    color,
                                                    UUID.randomUUID(),
                                                    DateProvider.now()));
    }




    public Tag(List<DomainEvent> events) {
        super(events);
    }


    public BoardId getBoardId() {
        return boardId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void changeColor(String color) {
        if (this.color.equals(color)){
            return;
        }
        apply(new TagEvents.TagColorChanged(boardId, id, color, UUID.randomUUID(), DateProvider.now()));

    }


    public void rename(String newName) {
        if (name.equals(newName)){
            return;
        }
        apply(new TagEvents.TagRenamed(boardId, id, newName, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    public void markAsDeleted(String userId) {
        apply(new TagEvents.TagDeleted(boardId, id, userId, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    protected void when(DomainEvent event) {
        switch (event){
            case TagEvents.TagCreated e -> {
                this.id = e.tagId();
                this.boardId = e.boardId();
                this.name = e.name();
                this.color = e.color();
                this.isDeleted = false;
            }
            case TagEvents.TagRenamed e -> {
                this.name = e.name();
            }
            case TagEvents.TagColorChanged e -> {
                this.color = e.color();
            }
            case TagEvents.TagDeleted e -> {
                this.isDeleted = true;
            }
            default -> throw new RuntimeException("Unsupported event type: " + event.getClass());
        }
    }

}

