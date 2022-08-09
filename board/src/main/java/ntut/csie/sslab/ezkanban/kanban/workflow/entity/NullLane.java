package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

public final class NullLane implements Lane {

    public final static LaneId ID = LaneId.NULL;
    private final LaneState state;

    public final static NullLane nullLane = new NullLane();
    private NullLane() {
        super();
        state = LaneState.create(ID, WorkflowId.valueOf("-1"), ID, "Null Lane", WipLimit.UNLIMIT, 0, LaneType.Standard);
        state.laneLayout(LaneLayout.Vertical);
    }

    public static NullLane getInstance() {
        return nullLane;
    }

    @Override
    public LaneState getState() {
        return state;
    }
}
