package ntut.csie.sslab.ezkanban.kanban.card.entity;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public interface CardEvents extends DomainEvent {

    BoardId boardId();
    WorkflowId workflowId();
    CardId cardId();

    default String aggregateId(){
        return cardId().id();
    }


    ///////////////////////////////////////////////////////////////

    record CardCreated(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId laneId,
            CardId cardId,
            String description,
            int order,
            String userId,
            UUID id,
            Date occurredOn
    ) implements CardEvents {}

    ///////////////////////////////////////////////////////////////

    record CardDescriptionChanged(
            BoardId boardId,
            WorkflowId workflowId,
            CardId cardId,
            String description,
            String userId,
            UUID id,
            Date occurredOn
    ) implements CardEvents {}

    ///////////////////////////////////////////////////////////////

    record CardDeleted(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId laneId,
            CardId cardId,
            String userId,
            UUID id,
            Date occurredOn
    ) implements CardEvents {}

    ///////////////////////////////////////////////////////////////

    record CardMoved(
            BoardId boardId,
            WorkflowId workflowId,
            CardId cardId,
            LaneId oldLaneId,
            LaneId newLaneId,
            int order,
            String userId,
            UUID id,
            Date occurredOn
    ) implements CardEvents {}

    ///////////////////////////////////////////////////////////////


    class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
        public static final String MAPPING_TYPE_PREFIX = "CardEvents$";

        public static final String CARD_CREATED = MAPPING_TYPE_PREFIX + "CardCreated";
        public static final String CARD_DESCRIPTION_CHANGED = MAPPING_TYPE_PREFIX + "CardDescriptionChanged";
        public static final String CARD_DELETED = MAPPING_TYPE_PREFIX + "CardDeleted";
        public static final String CARD_MOVED = MAPPING_TYPE_PREFIX + "CardMoved";

        private static final DomainEventTypeMapper mapper;

        static {
            mapper = new DomainEventTypeMapperImpl();
            mapper.put(CARD_CREATED, CardCreated.class);
            mapper.put(CARD_DESCRIPTION_CHANGED, CardDescriptionChanged.class);
            mapper.put(CARD_DELETED, CardDeleted.class);
            mapper.put(CARD_MOVED, CardMoved.class);
        }

        public static DomainEventTypeMapper getInstance(){
            return mapper;
        }

    }

    static DomainEventTypeMapper mapper(){
        return TypeMapper.getInstance();
    }
 }
