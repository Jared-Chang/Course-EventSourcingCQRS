package ntut.csie.sslab.ezkanban.kanban.workflow.entity;


import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.Date;
import java.util.UUID;

public interface WorkflowEvents extends DomainEvent {

    BoardId boardId();
    WorkflowId workflowId();

    default String aggregateId(){
        return workflowId().id();
    }

    /////////////////////////////////////////////////////////////

    record WorkflowCreated(
            BoardId boardId,
            WorkflowId workflowId,
            String workflowName,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record WorkflowRenamed(
            BoardId boardId,
            WorkflowId workflowId,
            String name,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record WorkflowDeleted(
            BoardId boardId,
            WorkflowId workflowId,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record StageCreated(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId parentId,
            LaneId stageId,
            String name,
            LaneType type,
            WipLimit wipLimit,
            int order,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record SwimLaneCreated(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId parentId,
            LaneId swimLaneId,
            String name,
            LaneType type,
            WipLimit wipLimit,
            int order,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record LaneRenamed(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId laneId,
            String name,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record WipLimitSet(
            BoardId boardId,
            WorkflowId workflowId,
            LaneId laneId,
            WipLimit wipLimit,
            String userId,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    record WorkflowMoved(
            BoardId boardId,
            WorkflowId workflowId,
            String userId,
            int order,
            UUID id,
            Date occurredOn
    ) implements WorkflowEvents {}
    ///////////////////////////////////////////////////////////////

    class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
        public static final String MAPPING_TYPE_PREFIX = "WorkflowEvents$";
        public static final String WORKFLOW_CREATED = MAPPING_TYPE_PREFIX + "WorkflowCreated";
        public static final String LANE_RENAMED = MAPPING_TYPE_PREFIX + "LaneRenamed";
        public static final String WIP_LIMIT_SET = MAPPING_TYPE_PREFIX + "WipLimitSet";
        public static final String STAGE_CREATED = MAPPING_TYPE_PREFIX + "StageCreated";
        public static final String SWIM_LANE_CREATED = MAPPING_TYPE_PREFIX + "SwimLaneCreated";
        public static final String WORKFLOW_DELETED = MAPPING_TYPE_PREFIX + "WorkflowDeleted";
        public static final String WORKFLOW_RENAMED = MAPPING_TYPE_PREFIX + "WorkflowRenamed";

        public static final String WORKFLOW_MOVED = MAPPING_TYPE_PREFIX + "WorkflowMoved";

        private static final DomainEventTypeMapper mapper;

        static {
            mapper = new DomainEventTypeMapperImpl();
            mapper.put(WORKFLOW_CREATED, WorkflowCreated.class);
            mapper.put(LANE_RENAMED, LaneRenamed.class);
            mapper.put(WIP_LIMIT_SET, WipLimitSet.class);
            mapper.put(STAGE_CREATED, StageCreated.class);
            mapper.put(SWIM_LANE_CREATED, SwimLaneCreated.class);
            mapper.put(WORKFLOW_DELETED, WorkflowDeleted.class);
            mapper.put(WORKFLOW_RENAMED, WorkflowRenamed.class);
        }

        public static DomainEventTypeMapper getInstance(){
            return mapper;
        }

    }

    static DomainEventTypeMapper mapper(){
        return TypeMapper.getInstance();
    }
}

