package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Stage.class)
public class Stage implements Lane {

    private final LaneState state;

    @JsonCreator
    public Stage(
            @JsonProperty("id") LaneId id,
            @JsonProperty("workflowId") WorkflowId workflowId,
            @JsonProperty("parentId") LaneId parentId,
            @JsonProperty("name") String name,
            @JsonProperty("wipLimit") WipLimit wipLimit,
            @JsonProperty("order") int order,
            @JsonProperty("type") LaneType type) {

        super();
        state = LaneState.create(id, workflowId, parentId, name, wipLimit, order, type);
        state.laneLayout(LaneLayout.Vertical);
    }

    @Override
    public LaneState getState() {
        return state;
    }


}
