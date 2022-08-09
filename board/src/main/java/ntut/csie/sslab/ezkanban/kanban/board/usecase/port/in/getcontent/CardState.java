package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.eclipse.persistence.internal.cache.Clearable;

import java.util.Date;

@JsonDeserialize(as = CardState.CardStateImpl.class)
public interface CardState extends Clearable {

    void cardId(CardId cardId);
    void boardId(BoardId boardId);
    void workflowId(WorkflowId workflowId);
    void laneId(LaneId laneId);
    void userId(String userId);
    void description(String description);
    void estimate(String estimate);
    void note(String note);
    void deadline(Date deadline);
    void isDeleted(boolean deleted);

    CardId cardId();
    BoardId boardId();
    WorkflowId workflowId();
    LaneId laneId();
    String userId();
    String description();
    String estimate();
    String note();
    Date deadline();
    boolean isDeleted();

    int version();
    void version(int version);
    void incVersion();

    static CardState create(){
        return new CardStateImpl();
    }

    class CardStateImpl implements CardState {
        private CardId cardId;
        private String userId;
        private BoardId boardId;
        private WorkflowId workflowId;
        private LaneId laneId;
        private String description;
        private String estimate;
        private String note;
        private Date deadline;
        private int version;
        @JsonIgnore
        private boolean isDeleted;

        public CardStateImpl(){
            isDeleted = false;
        }

        @Override
        public void cardId(CardId cardId) {
            this.cardId = cardId;
        }

        @Override
        public void description(String description) {
            this.description = description;
        }

        @Override
        public void estimate(String estimate) {
            this.estimate = estimate;
        }

        @Override
        public void note(String note) {
            this.note = note;
        }

        @Override
        public void deadline(Date deadline) {
            this.deadline = deadline;
        }

        @Override
        public CardId cardId() {
            return cardId;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public String estimate() {
            return estimate;
        }

        @Override
        public String note() {
            return note;
        }

        @Override
        public Date deadline() {
            return deadline;
        }

        @Override
        public String userId() {
            return userId;
        }

        @Override
        public void boardId(BoardId boardId) {
            this.boardId = boardId;
        }

        @Override
        public void userId(String userId) {
            this.userId = userId;
        }

        @Override
        public void workflowId(WorkflowId workflowId) {
            this.workflowId = workflowId;
        }

        @Override
        public void laneId(LaneId laneId) {
            this.laneId = laneId;
        }

        @Override
        public BoardId boardId() {
            return boardId;
        }

        @Override
        public WorkflowId workflowId() {
            return workflowId;
        }

        @Override
        public LaneId laneId() {
            return laneId;
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

        @Override
        public void clear() {
        }
    }
}
