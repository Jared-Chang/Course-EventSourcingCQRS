package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create;

import ntut.csie.sslab.ddd.usecase.Input;

public class CreateWorkflowInput implements Input {

    private String boardId;
    private String name;
    private String userId;
    private String username;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
