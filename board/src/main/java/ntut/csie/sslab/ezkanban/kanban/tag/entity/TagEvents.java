package ntut.csie.sslab.ezkanban.kanban.tag.entity;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.Date;
import java.util.UUID;

public interface TagEvents extends DomainEvent {

    BoardId boardId();
    String tagId();

    default String aggregateId(){
        return boardId().id();
    }

    ///////////////////////////////////////////////////////////////
    record TagCreated(
            BoardId boardId,
            String tagId,
            String name,
            String color,
            UUID id,
            Date occurredOn
    ) implements TagEvents {}

    record TagRenamed(
            BoardId boardId,
            String tagId,
            String name,
            UUID id,
            Date occurredOn
    ) implements TagEvents {}

    record TagColorChanged(
            BoardId boardId,
            String tagId,
            String color,
            UUID id,
            Date occurredOn
    ) implements TagEvents {}

    record TagDeleted(
            BoardId boardId,
            String tagId,
            String userId,
            UUID id,
            Date occurredOn
    ) implements TagEvents {}

    class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
        public static final String MAPPING_TYPE_PREFIX = "TagEvents$";
        public static final String TAG_CREATED = MAPPING_TYPE_PREFIX + "TagCreated";
        public static final String TAG_RENAMED = MAPPING_TYPE_PREFIX + "TagRenamed";
        public static final String TAG_COLOR_CHANGED = MAPPING_TYPE_PREFIX + "TagColorChanged";
        public static final String TAG_DELETED = MAPPING_TYPE_PREFIX + "TagDeleted";
        private static final DomainEventTypeMapper mapper;
        static {
            mapper = new DomainEventTypeMapperImpl();
            mapper.put(TAG_CREATED, TagEvents.TagCreated.class);
            mapper.put(TAG_RENAMED, TagEvents.TagRenamed.class);
            mapper.put(TAG_COLOR_CHANGED, TagEvents.TagColorChanged.class);
            mapper.put(TAG_DELETED, TagEvents.TagDeleted.class);
        }
        public static DomainEventTypeMapper getInstance(){
            return mapper;
        }
    }

    static DomainEventTypeMapper mapper(){
        return TagEvents.TypeMapper.getInstance();
    }

}
