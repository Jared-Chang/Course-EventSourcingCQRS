package ntut.csie.sslab.ezkanban.kanban.board.entity;

import ntut.csie.sslab.ddd.entity.ValueObject;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

public record CommittedWorkflow(BoardId boardId, WorkflowId workflowId, int order) implements ValueObject {
}
