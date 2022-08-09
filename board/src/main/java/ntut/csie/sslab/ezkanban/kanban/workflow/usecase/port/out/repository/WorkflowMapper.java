package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.WorkflowState;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.*;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneData;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneDto;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class WorkflowMapper {
    public static WorkflowData toData(Workflow workflow) {
        WorkflowData workflowData = new WorkflowData(
                workflow.getId().id(),
                workflow.getBoardId().id(),
                workflow.getName(),
                workflow.getVersion());

        workflowData.setLastUpdated(DateProvider.now());
        workflowData.setStreamName(workflow.getStreamName());
        workflowData.setDomainEventDatas(workflow.getDomainEvents().stream().map(DomainEventMapper::toData).collect(Collectors.toList()));

        List<LaneData> laneData = new ArrayList<>();
        for(var stage : workflow.getRootStages()) {
            laneData.add(LaneMapper.toData(stage));
        }
        laneData.forEach(workflowData::addLaneData);

        return workflowData;
    }


    public static List<WorkflowData> toData(List<Workflow> workflows) {
        List<WorkflowData> result = new ArrayList<>();
        workflows.forEach( x -> result.add(toData(x)));
        return result;
    }

    public static WorkflowDto toDto(Workflow workflow){
        WorkflowDto dto = new WorkflowDto();
        dto.setWorkflowId(workflow.getWorkflowId().id());
        dto.setBoardId(workflow.getBoardId().id());
        dto.setName(workflow.getName());
        dto.setVersion(workflow.getVersion());
        List<LaneDto> stageModels = new ArrayList<>();
        for(var stage : workflow.getRootStages()){
            LaneDto stageModel = LaneMapper.toDto(stage);
            stageModels.add(stageModel);
        }
        dto.setLanes(stageModels);
        return dto;
    }

    public static Workflow toDomain(WorkflowData workflowData) {
        requireNotNull("WorkflowData", workflowData);

        Workflow workflow = new Workflow(
                WorkflowId.valueOf(workflowData.getWorkflowId()),
                BoardId.valueOf(workflowData.getBoardId()),
                workflowData.getName(),
                "");
        workflow.setVersion(workflowData.getVersion());
        for (LaneData each : workflowData.getLaneDatas()) {
            workflow.createStage(NullLane.ID, LaneId.valueOf(each.getId()), each.getName(), WipLimit.valueOf(each.getWipLimit()), LaneType.valueOf(each.getType()), "");
            createSublanes(workflow, each);
        }
        workflow.clearDomainEvents();
        return workflow;
    }

    public static List<Workflow> toDomain(List<WorkflowData> workflowDatas) {
        requireNotNull("WorkflowData list", workflowDatas);

        List<Workflow> result = new ArrayList<>();
        workflowDatas.forEach( x -> result.add(toDomain(x)));
        return result;
    }

    public static WorkflowDto toDto(WorkflowData workflowData){
        requireNotNull("WorkflowData", workflowData);

        WorkflowDto dto = new WorkflowDto();
        dto.setWorkflowId(workflowData.getWorkflowId());
        dto.setBoardId(workflowData.getBoardId());
        dto.setName(workflowData.getName());
        dto.setVersion(workflowData.getVersion());
        List<LaneDto> stageModels = new ArrayList<>();
        List<LaneData> rootStages = workflowData.getLaneDatas().stream().filter(x -> x.getParent() == null).toList();
        for(LaneData stage : rootStages){
            LaneDto stageModel = LaneMapper.toDto(stage);
            stageModels.add(stageModel);
        }
        dto.setLanes(stageModels);
        return dto;
    }

    public static WorkflowDto toDto(WorkflowState workflow){
        WorkflowDto dto = new WorkflowDto();
        dto.setWorkflowId(workflow.workflowId().id());
        dto.setBoardId(workflow.boardId().id());
        dto.setName(workflow.name());
        dto.setVersion(workflow.version());
        List<LaneDto> stageModels = new ArrayList<>();
        for(var stage : workflow.rootStages()){
            LaneDto stageModel = LaneMapper.toDto(stage);
            stageModels.add(stageModel);
        }
        dto.setLanes(stageModels);
        return dto;
    }

    public static List<WorkflowDto> toDto(List<WorkflowState> workflows){
        List<WorkflowDto> result = new ArrayList<>();
        workflows.forEach( x -> result.add(toDto(x)));
        return result;
    }

    private static void createSublanes(Workflow workflow, LaneData parent) {
        for (LaneData each : parent.getChildren()) {
            if(each.getLayout().equals(LaneLayout.Vertical.name()))
                workflow.createStage(LaneId.valueOf(parent.getId()), LaneId.valueOf(each.getId()), each.getName(), WipLimit.valueOf(each.getWipLimit()), LaneType.valueOf(each.getType()), "");
            else
                workflow.createSwimLane(LaneId.valueOf(parent.getId()), LaneId.valueOf(each.getId()), each.getName(), WipLimit.valueOf(each.getWipLimit()), LaneType.valueOf(each.getType()), "");
            createSublanes(workflow, each);
        }
    }

    private static OutboxMapper mapper = new Mapper();
    public static OutboxMapper newMapper(){
        return mapper;
    }
    static class Mapper implements OutboxMapper<Workflow, WorkflowData>{

        @Override
        public Workflow toDomain(WorkflowData data) {
            return WorkflowMapper.toDomain(data);
        }

        @Override
        public WorkflowData toData(Workflow aggregateRoot) {
            return WorkflowMapper.toData(aggregateRoot);
        }
    }
}
