package ntut.csie.sslab.ezkanban.kanban.board.entity;


import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.Date;
import java.util.UUID;

public interface BoardEvents extends DomainEvent {

    BoardId boardId();

    default String aggregateId(){
        return boardId().id();
    }

    ///////////////////////////////////////////////////////////////
    record BoardCreated(
            String teamId,
            BoardId boardId,
            String boardName,
            UUID id,
            Date occurredOn
    ) implements BoardEvents  {}

    ///////////////////////////////////////////////////////////////
//
//    record BoardEntered(
//            String userId,
//            BoardId boardId,
//            BoardSessionId boardSessionId,
//            UUID id,
//            Instant occurredOn
//    ) implements BoardEvents {}
//
//    ///////////////////////////////////////////////////////////////
//    record BoardLeft(
//            String userId,
//            BoardId boardId,
//            BoardSessionId boardSessionId,
//            UUID id,
//            Instant occurredOn
//    ) implements BoardEvents {}

    ///////////////////////////////////////////////////////////////

    record BoardMemberAdded(
            String userId,
            BoardId boardId,
            BoardRole boardRole,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}

    ///////////////////////////////////////////////////////////////

    record BoardMemberRemoved(
            String userId,
            BoardId boardId,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}
    ///////////////////////////////////////////////////////////////

    record BoardRenamed(
            String teamId,
            BoardId boardId,
            String boardName,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}


    ///////////////////////////////////////////////////////////////

    record WorkflowCommitted(
            BoardId boardId,
            WorkflowId workflowId,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}


    ///////////////////////////////////////////////////////////////

    record WorkflowUncommitted(
            BoardId boardId,
            WorkflowId workflowId,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}

    ///////////////////////////////////////////////////////////////

    record WorkflowMoved(
            BoardId boardId,
            WorkflowId workflowId,
            String userId,
            int order,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}


    ///////////////////////////////////////////////////////////////

    record BoardDeleted(
            String teamId,
            BoardId boardId,
            String userId,
            UUID id,
            Date occurredOn
    ) implements BoardEvents {}
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////


    class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
        public static final String MAPPING_TYPE_PREFIX = "BoardEvents$";
        public static final String BOARD_CREATED = MAPPING_TYPE_PREFIX + "BoardCreated";
        public static final String BOARD_ENTERED = MAPPING_TYPE_PREFIX + "BoardEntered";
        public static final String BOARD_LEFT = MAPPING_TYPE_PREFIX + "BoardLeft";
        public static final String BOARD_MEMBER_ADDED = MAPPING_TYPE_PREFIX + "BoardMemberAdded";
        public static final String BOARD_MEMBER_REMOVED = MAPPING_TYPE_PREFIX + "BoardMemberRemoved";
        public static final String BOARD_RENAMED = MAPPING_TYPE_PREFIX + "BoardRenamed";
        public static final String WORKFLOW_COMMITTED = MAPPING_TYPE_PREFIX + "WorkflowCommitted";
        public static final String WORKFLOW_UNCOMMITTED = MAPPING_TYPE_PREFIX + "WorkflowUncommitted";
        public static final String WORKFLOW_MOVED = MAPPING_TYPE_PREFIX + "WorkflowMoved";
        public static final String BOARD_DELETED = MAPPING_TYPE_PREFIX + "BoardDeleted";

        private static final DomainEventTypeMapper mapper;

        static {
            mapper = new DomainEventTypeMapperImpl();
            mapper.put(BOARD_CREATED, BoardCreated.class);
            mapper.put(BOARD_MEMBER_ADDED, BoardMemberAdded.class);
            mapper.put(BOARD_MEMBER_REMOVED, BoardMemberRemoved.class);
            mapper.put(BOARD_RENAMED, BoardRenamed.class);
            mapper.put(WORKFLOW_COMMITTED, WorkflowCommitted.class);
            mapper.put(WORKFLOW_UNCOMMITTED, WorkflowUncommitted.class);
            mapper.put(WORKFLOW_MOVED, WorkflowMoved.class);
            mapper.put(BOARD_DELETED, BoardDeleted.class);
        }

        public static DomainEventTypeMapper getInstance(){
            return mapper;
        }

    }

    static DomainEventTypeMapper mapper(){
        return TypeMapper.getInstance();
    }

}
