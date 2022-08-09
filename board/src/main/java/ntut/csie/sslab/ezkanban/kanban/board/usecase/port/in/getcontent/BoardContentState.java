package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntut.csie.sslab.ddd.usecase.IdempotentData;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JsonDeserialize(as = BoardContentState.BoardContentStateImpl.class)
public interface BoardContentState {
    BoardState boardState();
    List<WorkflowState> workflowStates();
    Map<LaneId, List<CardState>> committedCardStates();
    long boardVersion();
    IdempotentData idempotentData();

    static BoardContentState create() {
        return new BoardContentStateImpl();
    }

    class BoardContentStateImpl implements BoardContentState {
        private BoardState boardState;
        private List<WorkflowState> workflowStates;
        private Map<LaneId, List<CardState>> committedCardStates;
        private long boardVersion;

        private IdempotentData idempotentData;

        public BoardContentStateImpl() {
            boardState = BoardState.create();
            workflowStates = new LinkedList<>();
            committedCardStates = new HashMap<>();
            boardVersion = 0;
            idempotentData = new IdempotentData();
        }

        public BoardContentStateImpl(BoardState boardState, List<WorkflowState> workflowStates, Map<LaneId, List<CardState>> committedCardStates) {
            this.boardState = boardState;
            this.workflowStates = workflowStates;
            this.committedCardStates = committedCardStates;
        }

        @Override
        public BoardState boardState() {
            return boardState;
        }

        @Override
        public List<WorkflowState> workflowStates() {
            return workflowStates;
        }

        @Override
        public long boardVersion() {
            return boardVersion;
        }

        @Override
        public Map<LaneId, List<CardState>> committedCardStates() {
            return committedCardStates;
        }

        @Override
        public IdempotentData idempotentData() {
            return idempotentData;
        }
    }
}

