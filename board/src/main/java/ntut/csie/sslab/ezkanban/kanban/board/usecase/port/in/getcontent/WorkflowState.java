package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Lane;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.eclipse.persistence.internal.cache.Clearable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonDeserialize(as = WorkflowState.WorkflowStateImpl.class)
public interface WorkflowState extends Clearable {
    void workflowId(WorkflowId workflowId);
    void boardId(BoardId boardId);
    void name(String name);
    void isDeleted(boolean deleted);
    WorkflowId workflowId();
    String name();
    BoardId boardId();
    boolean isDeleted();

    void rootStages(List<Lane> rootStages);
    List<Lane> rootStages();

    int version();
    void version(int version);
    void incVersion();

    static WorkflowState create(){
        return new WorkflowStateImpl();
    }

    class WorkflowStateImpl implements WorkflowState {
        private WorkflowId workflowId;
        private BoardId boardId;
        private String name;
        private List<Lane> rootStages;
        private int version;
        @JsonIgnore
        private boolean isDeleted;

        public WorkflowStateImpl(){
            super();
            rootStages = Collections.synchronizedList(new ArrayList<>());
            version = 0;
            isDeleted = false;
        }

        @Override
        public void workflowId(WorkflowId workflowId) {
            this.workflowId = workflowId;
        }

        @Override
        public void name(String name) {
            this.name = name;
        }

        @JsonIgnore
        @Override
        public boolean isDeleted() {
            return isDeleted;
        }

        @JsonIgnore
        @Override
        public void isDeleted(boolean deleted) {
            this.isDeleted = deleted;
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
        public void boardId(BoardId boardId) {
            this.boardId = boardId;
        }

        @Override
        public BoardId boardId() {
            return boardId;
        }

        @Override
        public void rootStages(List<Lane> rootStages) {
            this.rootStages = rootStages;
        }

        @Override
        public void clear() {
            rootStages.clear();
        }

        @Override
        public List<Lane> rootStages() {
            return rootStages;
        }

        @Override
        public int version() {
            return version;
        }

        @Override
        public void version(int version) {
            this.version = version;
        }

        @Override
        public void incVersion() {
            version++;
        }
    }
}
