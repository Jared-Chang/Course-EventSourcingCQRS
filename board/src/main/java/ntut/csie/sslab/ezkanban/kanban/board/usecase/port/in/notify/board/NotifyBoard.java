package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.board;

import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;

public interface NotifyBoard {
    void whenWorkflowCreated(WorkflowEvents.WorkflowCreated workflowCreated);
    void whenWorkflowDeleted(WorkflowEvents.WorkflowDeleted workflowDeleted);
}
