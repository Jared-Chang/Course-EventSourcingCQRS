package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.*;

public class Workflow extends AggregateRoot<WorkflowId,DomainEvent> {

    public final static String CATEGORY = "Workflow";

    private BoardId boardId;
    private String name;
    private List<Lane> rootStages;


    public Workflow(List<? extends DomainEvent> domainEvents){
        super(domainEvents);
    }

    public Workflow(WorkflowId workflowId, BoardId boardId, String name, String userId) {
        super();
        apply(new WorkflowEvents.WorkflowCreated(
                boardId,
                workflowId,
                name,
                userId,
                UUID.randomUUID(),
                DateProvider.now()));
    }

    public void rename(String newName, String userId) {

        apply(new WorkflowEvents.WorkflowRenamed(
                boardId,
                getWorkflowId(),
                newName,
                userId,
                UUID.randomUUID(),
                DateProvider.now()));;
    }

    public void createStage(LaneId parentId, LaneId laneId, String name, WipLimit wipLimit, LaneType laneType, String userId) {

        // require


        LaneId resultParentId;
        int order;
        if (parentId.isNull()){
            resultParentId = NullLane.ID;
            order = rootStages.size();
        }
        else {
            resultParentId = parentId;
            order = getLaneById(parentId).get().getChildren().size();
        }

        apply(new WorkflowEvents.StageCreated(
                boardId,
                getWorkflowId(),
                resultParentId,
                laneId,
                name,
                laneType,
                wipLimit,
                order,
                userId,
                UUID.randomUUID(),
                DateProvider.now()));

        // ensure ....
    }

    public void createSwimLane(LaneId parentId, LaneId laneId, String name, WipLimit wipLimit, LaneType type, String userId) {

        apply(new WorkflowEvents.SwimLaneCreated(
                boardId,
                getWorkflowId(),
                parentId,
                laneId,
                name,
                type,
                wipLimit,
                getLaneById(parentId).get().getChildren().size(),
                userId,
                UUID.randomUUID(),
                DateProvider.now()));
    }

    public void renameLane(LaneId laneId, String newName, String userId) {
        var lane = getLaneById(laneId).get();
        if(lane.getName().equals(newName))
            return;

        apply(new WorkflowEvents.LaneRenamed(
                boardId,
                getWorkflowId(),
                laneId,
                newName,
                userId,
                UUID.randomUUID(),
                DateProvider.now()));
    }

    public void setLaneWipLimit(LaneId laneId, WipLimit newWipLimit, String userId) {
        var lane = getLaneById(laneId).get();
        if(lane.getWipLimit().equals(newWipLimit))
            return;

        apply(new WorkflowEvents.WipLimitSet(
                boardId,
                getWorkflowId(),
                laneId,
                newWipLimit,
                userId,
                UUID.randomUUID(),
                DateProvider.now()));
    }


    public String getName() {
        return name;
    }

    public WorkflowId getWorkflowId() {
        return getId();
    }

    public BoardId getBoardId() {
        return boardId;
    }

    public List<Lane> getRootStages() {
        return rootStages;
    }

    public Optional<Lane> getLaneById(LaneId laneId) {
        Optional<Lane> targetLane;
        for (var stage: rootStages) {
            targetLane = stage.getLaneById(laneId);
            if(targetLane.isPresent()) {
                return targetLane;
            }
        }
        return Optional.empty();
    }

    public List<Lane> getAllLanes() {
        List<Lane> result = new ArrayList<>(getRootStages());
        for (var each : getRootStages()) {
            result.addAll(each.getAllLanes());
        }
        return Collections.unmodifiableList(result);
    }


    public void markAsDeleted(String userId) {
        apply(new WorkflowEvents.WorkflowDeleted(
                boardId,
                getWorkflowId(),
                userId,
                UUID.randomUUID(),
                DateProvider.now()));
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    private void insertRootStage(Lane lane, int order) {
        rootStages.add(order, lane);
        lane.setParentId(NullLane.ID);
        reorderRootStage();
    }

    private void reorderRootStage() {
        for(int i = 0; i < rootStages.size() ; i++){
            rootStages.get(i).setOrder(i);
        }
    }

    @Override
    protected void when(DomainEvent domainEvent) {
        switch (domainEvent) {
            case WorkflowEvents.WorkflowCreated event -> {
                isDeleted = false;
                id = event.workflowId();
                boardId = event.boardId();
                name = event.workflowName();
                rootStages = new LinkedList<>();
            }
            case WorkflowEvents.WorkflowRenamed event -> this.name = event.name();
            case WorkflowEvents.StageCreated event ->{
                if (event.parentId().isNull()){
                    Lane stage = LaneBuilder.newInstance()
                            .workflowId(event.workflowId())
                            .parentId(NullLane.ID)
                            .laneId(event.stageId())
                            .name(event.name())
                            .wipLimit(event.wipLimit())
                            .type(event.type())
                            .stage()
                            .order(event.order())
                            .build();
                    insertRootStage(stage, stage.getOrder());
                }
                else {
                    Lane parentLane = getLaneById(event.parentId()).get();
                    parentLane.createStage(event.stageId(), event.name(), event.wipLimit(), event.type());
                }
            }
            case WorkflowEvents.SwimLaneCreated event -> {
                Lane lane = getLaneById(event.parentId()).get();
                lane.createSwimLane(event.swimLaneId(), event.name(), event.wipLimit(), event.type());
            }
            case WorkflowEvents.WorkflowDeleted event -> {
                rootStages.clear();
                this.isDeleted = true;
            }
            case WorkflowEvents.LaneRenamed event -> {
                Lane lane = getLaneById(event.laneId()).get();
                lane.rename(event.name());
            }
            case WorkflowEvents.WipLimitSet event -> {
                Lane lane = getLaneById(event.laneId()).get();
                lane.setWipLimit(event.wipLimit());
            }
            default -> {
            }
        }
    }

}
