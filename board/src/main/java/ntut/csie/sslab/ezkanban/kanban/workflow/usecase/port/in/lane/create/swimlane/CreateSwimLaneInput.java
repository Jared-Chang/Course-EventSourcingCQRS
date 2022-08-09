package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane;

import ntut.csie.sslab.ddd.usecase.Input;

public class CreateSwimLaneInput implements  Input {
    private String workflowId;
    private String parentId;
    private String name;
    private int wipLimit;
    private String laneType;
    private String userId;
    private String boardId;
    private long version;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWipLimit() {
        return wipLimit;
    }

    public void setWipLimit(int wipLimit) {
        this.wipLimit = wipLimit;
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
