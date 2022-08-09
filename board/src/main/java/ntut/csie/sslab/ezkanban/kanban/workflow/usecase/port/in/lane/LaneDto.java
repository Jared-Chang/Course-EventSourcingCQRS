package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane;

import java.util.List;

public class LaneDto {

    private String laneId;
    private String workflowId;
    private String parentId;
    private String name;
    private int wipLimit;
    private int order;
    private String type;
    private String layout;
    private List<LaneDto> lanes;
    public LaneDto(){

    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getCountOfSubStage() {
        int stageCounts = 0;
        for(LaneDto each :lanes) {
            if(each.layout == "Vertical") {
                stageCounts++;
            }
        }
        return stageCounts;
    }

    public int getCountOfSubSwimLane() {
        int swimLaneCounts = 0;
        for(LaneDto each :lanes) {
            if(each.layout == "Horizontal") {
                swimLaneCounts++;
            }
        }
        return swimLaneCounts;
    }

    public List<LaneDto> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneDto> lanes) {
        this.lanes = lanes;
    }


}
