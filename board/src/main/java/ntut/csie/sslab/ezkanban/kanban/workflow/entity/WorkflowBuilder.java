package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.UUID;

public class WorkflowBuilder {

    private String id;
    private String boardId;
    private String name;
    private String userId;

    public static WorkflowBuilder newInstance() {
        return new WorkflowBuilder();
    }

    public WorkflowBuilder boardId(String boardId) {
        this.boardId = boardId;
        return this;
    }

    public WorkflowBuilder boardId(BoardId boardId) {
        this.boardId = boardId.id();
        return this;
    }

    public WorkflowBuilder name(String name) {
        this.name = name;
        return this;
    }

    public WorkflowBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public Workflow build() {
        id = UUID.randomUUID().toString();
        Workflow workflow = new Workflow(WorkflowId.valueOf(id), BoardId.valueOf(boardId), name, userId);
        return workflow;
    }
}