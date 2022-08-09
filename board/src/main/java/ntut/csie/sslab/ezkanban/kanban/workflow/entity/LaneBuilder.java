package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

public class LaneBuilder {

    private LaneId id;
    private String workflowId;
    private LaneId parentId;
    private String name;
    private int order;
    private WipLimit wipLimit;
    private LaneType type;
    private LaneLayout laneLayout;

    public static LaneBuilder newInstance() {
        return new LaneBuilder();
    }

    public LaneBuilder workflowId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    public LaneBuilder workflowId(WorkflowId workflowId) {
        this.workflowId = workflowId.id();
        return this;
    }

    public LaneBuilder parentId(LaneId parentId) {
        this.parentId = parentId;
        return this;
    }

    public LaneBuilder laneId(LaneId laneId) {
        this.id = laneId;
        return this;
    }


    public LaneBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LaneBuilder type(LaneType type) {
        this.type = type;
        return this;
    }

    public LaneBuilder order(int order) {
        this.order = order;
        return this;
    }

    public LaneBuilder wipLimit(int wipLimit) {
        this.wipLimit = new WipLimit(wipLimit);
        return this;
    }

    public LaneBuilder wipLimit(WipLimit wipLimit) {
        this.wipLimit = wipLimit;
        return this;
    }


    public LaneBuilder stage() {
        this.laneLayout = LaneLayout.Vertical;
        return this;
    }

    public LaneBuilder swimLane() {
        this.laneLayout = LaneLayout.Horizontal;
        return this;
    }

    public Lane build() {
        Lane lane;

        if(id == null) {
            throw new RuntimeException("Lane id can not be null.");
        }
        if(laneLayout == null){
            throw new RuntimeException("LaneLayout can not be null.");
        }else if(laneLayout == LaneLayout.Vertical){
            lane = new Stage(id, WorkflowId.valueOf(workflowId), parentId, name, wipLimit, order, type);
        }else {
            lane = new SwimLane(id, WorkflowId.valueOf(workflowId), parentId, name, wipLimit, order, type);
        }
        return lane;
    }

}
