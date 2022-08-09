package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneDto;

import java.util.List;

public class WorkflowDto {
    private String workflowId;
    private String boardId;
    private String name;
    private List<LaneDto> lanes;
    private long version;

    public WorkflowDto() {

    }

    public WorkflowDto(String workflowId, String boardId, String name, List<LaneDto> lanes, long version) {
        this.workflowId = workflowId;
        this.boardId = boardId;
        this.name = name;
        this.lanes = lanes;
        this.version = version;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LaneDto> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneDto> lanes) {
        this.lanes = lanes;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}