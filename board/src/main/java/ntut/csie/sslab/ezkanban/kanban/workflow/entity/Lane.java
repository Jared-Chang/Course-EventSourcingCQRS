package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntut.csie.sslab.ddd.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@JsonDeserialize(using = LaneInstanceDeserializer.class)
public interface Lane extends Entity<LaneId> {
    @JsonIgnore
    LaneState getState();

    default void removeLanes() {
        getState().clear();
    }

    default Optional<Lane> getLaneById(LaneId laneId) {
        if (getId().equals(laneId)) {
            return Optional.of(this);
        }

        for (Lane each : getChildren()) {
            Optional<Lane> targetLane = each.getLaneById(laneId);
            if (targetLane.isPresent()) {
                return targetLane;
            }
        }

        return Optional.empty();
    }

    default Optional<Lane> getChild(LaneId laneId) {
        return getChildren().stream().filter(x -> x.getId().equals(laneId)).findFirst();
    }

    default void rename(String newName) {
        getState().name(newName);
    }

    default Lane createStage(LaneId laneId, String name, WipLimit wipLimit, LaneType type) {
        Lane stage = LaneBuilder.newInstance()
                .workflowId(getState().workflowId())
                .laneId(laneId)
                .parentId(getId())
                .name(name)
                .order(getState().children().size())
                .wipLimit(wipLimit.value())
                .type(type)
                .stage()
                .build();
        getState().children().add(stage);

        return stage;
    }

    default Lane createSwimLane(LaneId laneId, String name, WipLimit wipLimit, LaneType type) {
        Lane swimLane = LaneBuilder.newInstance()
                .parentId(getId())
                .workflowId(getState().workflowId())
                .laneId(laneId)
                .name(name)
                .order(getState().children().size())
                .wipLimit(wipLimit.value())
                .type(type)
                .swimLane()
                .build();

        this.getState().children().add(swimLane);
        return swimLane;
    }

    default void setOrder(int order) {
        getState().order(order);
    }

    default void setWipLimit(WipLimit wipLimit) {
        getState().wipLimit(wipLimit);
    }

    default void setParentId(LaneId parentId) {
        getState().parentId(parentId);
    }

    default void setType(LaneType type) {
        getState().laneType(type);
    }

    default void sortChildren() {
        getState().children().sort(Comparator.comparing(Lane::getOrder));
    }

    default void addChildren(List<Lane> children) {
        getState().children().addAll(children);
        sortChildren();
    }

    default void appendChild(Lane lane) {
        lane.setOrder(getState().children().size());
        lane.setParentId(this.getId());
        getState().children().add(lane);
    }

    @JsonIgnore
    default boolean isRoot() {
        return getState().parentId().equals(NullLane.ID);
    }

    default String getName() {
        return getState().name();
    }

    default WorkflowId getWorkflowId() {
        return getState().workflowId();
    }

    default LaneId getParentId() {
        return getState().parentId();
    }

    default WipLimit getWipLimit() {
        return getState().wipLimit();
    }

    default int getOrder() {
        return getState().order();
    }

    default LaneType getType() {
        return getState().laneType();
    }

    default List<Lane> getChildren() {
        return getState().children();
    }

    default LaneLayout getLayout() {
        return getState().laneLayout();
    }

    default void setLayout(LaneLayout layout) {
        getState().laneLayout(layout);
    }

    @JsonIgnore
    default boolean isStage() {
        return getLayout() == LaneLayout.Vertical;
    }

    @JsonIgnore
    default boolean isSwimLane() {
        return getLayout() == LaneLayout.Horizontal;
    }

    default void deleteLane(Lane lane) {
        getState().children().remove(lane);
        reorderChildren();
    }

    default void insertLane(Lane lane, int order) {
        getState().children().add(order, lane);
        lane.setParentId(this.getId());
        reorderChildren();
    }

    private void reorderChildren() {
        for (int i = 0; i < getState().children().size(); i++)
            getState().children().get(i).setOrder(i);
    }

    @JsonIgnore
    default List<Lane> getAllLanes() {
        List<Lane> result = new ArrayList<>();

        result.addAll(getChildren());
        for (Lane each : getChildren()) {
            result.addAll(each.getAllLanes());
        }

        return result;
    }

    @JsonIgnore
    default boolean isCopiedStructureEqual(Lane copiedLane) {
        if (getWorkflowId().equals(copiedLane.getWorkflowId()) &&
                getName().equals(copiedLane.getName()) &&
                getWipLimit().equals(copiedLane.getWipLimit()) &&
                getType().equals(copiedLane.getType()) &&
                getLayout().equals(copiedLane.getLayout()) &&
                getChildren().size() == copiedLane.getChildren().size()) {
            for (int i = 0; i < getChildren().size(); i++) {
                if (!getChildren().get(i).isCopiedStructureEqual(copiedLane.getChildren().get(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    default LaneId getId() {
        return getState().laneId();
    }


    @JsonDeserialize(as = LaneStateImpl.class)
    interface LaneState {

        void workflowId(WorkflowId workflowId);

        void name(String name);

        void parentId(LaneId parentId);

        void wipLimit(WipLimit wipLimit);

        void order(int order);

        void laneType(LaneType laneType);

        void laneLayout(LaneLayout laneLayout);

        void clear();

        LaneId laneId();

        WorkflowId workflowId();

        String name();

        LaneId parentId();

        WipLimit wipLimit();

        int order();

        LaneType laneType();

        LaneLayout laneLayout();

        List<Lane> children();


        static LaneState create(LaneId laneId, WorkflowId workflowId, LaneId parentId, String name, WipLimit wipLimit, int order, LaneType type) {
            return new LaneStateImpl(laneId, workflowId, parentId, name, wipLimit, order, type);
        }
    }


    class LaneStateImpl implements LaneState {
        private final LaneId laneId;
        private WorkflowId workflowId;
        private String name;
        private LaneId parentId;
        private WipLimit wipLimit;
        private int order;
        private LaneType type;
        private LaneLayout layout;
        private final List<Lane> children;

        @JsonCreator
        public LaneStateImpl(@JsonProperty("laneId") LaneId laneId,
                             @JsonProperty("workflowId") WorkflowId workflowId,
                             @JsonProperty("parentId") LaneId parentId,
                             @JsonProperty("name") String name,
                             @JsonProperty("wipLimit") WipLimit wipLimit,
                             @JsonProperty("order") int order,
                             @JsonProperty("type") LaneType type) {
            super();
            this.laneId = laneId;
            this.name = name;
            this.workflowId = workflowId;
            this.parentId = parentId;
            this.wipLimit = wipLimit;
            this.order = order;
            this.type = type;
            this.children = new ArrayList<>();
        }

        @Override
        public void workflowId(WorkflowId workflowId) {
            this.workflowId = workflowId;
        }

        @Override
        public void name(String name) {
            this.name = name;
        }

        @Override
        public void parentId(LaneId parentId) {
            this.parentId = parentId;
        }

        @Override
        public void wipLimit(WipLimit wipLimit) {
            this.wipLimit = wipLimit;
        }

        @Override
        public void order(int order) {
            this.order = order;
        }

        @Override
        public void laneType(LaneType laneType) {
            this.type = laneType;
        }

        @Override
        public void laneLayout(LaneLayout laneLayout) {
            this.layout = laneLayout;
        }


        @Override
        public void clear() {
            children.clear();
        }

        @Override
        public LaneId laneId() {
            return laneId;
        }

        @Override
        public WorkflowId workflowId() {
            return workflowId;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public LaneId parentId() {
            return parentId;
        }

        @Override
        public WipLimit wipLimit() {
            return wipLimit;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public LaneType laneType() {
            return type;
        }

        @Override
        public LaneLayout laneLayout() {
            return layout;
        }

        @Override
        public List<Lane> children() {
            return children;
        }
    }
}
