package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommittedWorkflowDataId implements Serializable {
    @Column(name = "board_id", nullable = false)
    private String boardId;

    @Column(name = "workflow_id", nullable = false)
    private String workflowId;

    public CommittedWorkflowDataId() {
    }

    public CommittedWorkflowDataId(String boardId, String workflowId) {
        this.boardId = boardId;
        this.workflowId = workflowId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommittedWorkflowDataId that)) return false;
        return Objects.equals(getWorkflowId(), that.getWorkflowId()) &&
                Objects.equals(getBoardId(), that.getBoardId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkflowId(), getBoardId());
    }
}
