package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

@Entity
@Table(name="committed_workflow")
public class CommittedWorkflowData {

    @EmbeddedId
    private CommittedWorkflowDataId committedWorkflowDataId;

    @Column(name="workflow_order")
    private int order;

    public CommittedWorkflowData(){}

    public CommittedWorkflowData(CommittedWorkflowDataId committedWorkflowDataId, int order) {
        this.committedWorkflowDataId = committedWorkflowDataId;
        this.order = order;
    }

    public String getBoardId() {
        return committedWorkflowDataId.getBoardId();
    }

    public void setBoardId(String boardId) {
        requireNotNull("Board id", boardId);

        committedWorkflowDataId.setBoardId(boardId);
    }

    public String getWorkflowId() {
        return committedWorkflowDataId.getWorkflowId();
    }

    public void setWorkflowId(String workflowId) {
        requireNotNull("Workflow id", workflowId);

        committedWorkflowDataId.setWorkflowId(workflowId);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
