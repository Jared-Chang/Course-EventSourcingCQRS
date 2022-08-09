package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class WorkflowTest {
    private String userId = "userId";

    private Workflow createWorkflow() {
        Workflow workflow = new Workflow(
                WorkflowId.create(),
                BoardId.valueOf("boardId"),
                "First workflow",
                userId);

        return workflow;
    }

    private Lane createRootStage(Workflow workflow) {
        LaneId laneId = LaneId.create();
        workflow.createStage(NullLane.ID, laneId, "backlog", WipLimit.UNLIMIT, LaneType.Backlog, userId);
        return workflow.getLaneById(laneId).get();
    }

    private Lane createSubStage(Workflow workflow, Lane parent) {
        LaneId laneId = LaneId.create();
        workflow.createStage(parent.getId(), laneId, "backlog", WipLimit.UNLIMIT, LaneType.Backlog, userId);
        return workflow.getLaneById(laneId).get();
    }

    private Lane createSubSwimlane(Workflow workflow, Lane parent) {
        LaneId laneId = LaneId.create();
        workflow.createSwimLane(parent.getId(), laneId, "backlog", WipLimit.UNLIMIT, LaneType.Backlog, userId);
        return workflow.getLaneById(laneId).get();
    }

    @Test
    public void create_a_valid_workflow() {
        Workflow workflow = new Workflow(
                WorkflowId.create(),
                BoardId.valueOf("boardId"),
                "First workflow",
                userId);

        assertNotNull(workflow);
        Assertions.assertEquals(BoardId.valueOf("boardId"), workflow.getBoardId());
        assertEquals("First workflow", workflow.getName());
        assertFalse( workflow.isDeleted());
        assertEquals(0, workflow.getRootStages().size());
    }

    @Test
    public void delete_a_workflow_marks_it_as_deleted() {
        Workflow workflow = createWorkflow();

        workflow.markAsDeleted(userId);

        assertTrue(workflow.isDeleted());
    }

    @Test
    public void create_a_valid_root_stage(){
        Workflow workflow = createWorkflow();
        Lane expectedRootStage = new Stage(LaneId.create(), workflow.getWorkflowId(), NullLane.ID, "backlog", WipLimit.UNLIMIT, 0, LaneType.Backlog);

        workflow.createStage(
                expectedRootStage.getParentId(),
                expectedRootStage.getId(),
                expectedRootStage.getName(),
                expectedRootStage.getWipLimit(),
                expectedRootStage.getType(),
                userId);

        assertEquals(1, workflow.getRootStages().size());
        assertLaneEquals(expectedRootStage, workflow.getRootStages().get(0));
    }

    @Test
    public void create_a_valid_sub_stage_under_a_root_stage() {
        Workflow workflow = createWorkflow();
        Lane rootStage = createRootStage(workflow);
        Lane expectedSubStage = new Stage(LaneId.create(), workflow.getWorkflowId(), rootStage.getId(), "subStage", WipLimit.valueOf(2), 0, LaneType.Standard);

        workflow.createStage(
                expectedSubStage.getParentId(),
                expectedSubStage.getId(),
                expectedSubStage.getName(),
                expectedSubStage.getWipLimit(),
                expectedSubStage.getType(),
                userId);

        assertEquals(1, rootStage.getChildren().size());
        assertLaneEquals(expectedSubStage, rootStage.getChildren().get(0));
    }

    @Test
    public void create_a_valid_swimlane_under_a_root_stage() {
        Workflow workflow = createWorkflow();
        Lane rootStage = createRootStage(workflow);
        Lane expectedSwimLane = new SwimLane(LaneId.create(), workflow.getId(), rootStage.getId(), "swimLane", WipLimit.valueOf(3), 0, LaneType.Archive);

        workflow.createSwimLane(
                expectedSwimLane.getParentId(),
                expectedSwimLane.getId(),
                expectedSwimLane.getName(),
                expectedSwimLane.getWipLimit(),
                expectedSwimLane.getType(),
                userId);

        assertEquals(1, rootStage.getChildren().size());
        assertLaneEquals(expectedSwimLane, rootStage.getChildren().get(0));
    }

    @Test
    public void rename_a_root_stage() {
        Workflow workflow = createWorkflow();
        Lane rootStage = createRootStage(workflow);

        workflow.renameLane(rootStage.getId(), "newName", userId);

        assertEquals("newName", workflow.getLaneById(rootStage.getId()).get().getName());
    }


    @Test
    public void get_lane_by_non_existing_id_returns_an_empty_lane() {
        Workflow workflow = createWorkflow();

        Optional<Lane> lane = workflow.getLaneById(LaneId.valueOf("123"));

        assertFalse(lane.isPresent());
    }

    @Test
    public void rename_a_workflow(){
        Workflow workflow = createWorkflow();

        workflow.rename("newWorkflowName", userId);

        assertEquals("newWorkflowName", workflow.getName());
    }

    @Test
    public void set_valid_wip_limit_in_root_stage(){
        Workflow workflow = createWorkflow();
        Lane rootStage = createRootStage(workflow);

        workflow.setLaneWipLimit(rootStage.getId(), WipLimit.valueOf(5), userId);

        assertEquals(5, workflow.getLaneById(rootStage.getId()).get().getWipLimit().value());
    }

    private void assertLaneEquals(Lane expected, Lane actual) {
        assertEquals(expected.getWorkflowId(), actual.getWorkflowId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getParentId(), actual.getParentId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getWipLimit(), actual.getWipLimit());
        assertEquals(expected.getOrder(), actual.getOrder());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getLayout(), actual.getLayout());
        assertEquals(expected.getChildren().size(), actual.getChildren().size());
    }

}
