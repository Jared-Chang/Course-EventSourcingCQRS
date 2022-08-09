package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ddd.adapter.presenter.ViewModel;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardMemberDto;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardDto;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardContentViewModel implements ViewModel {
    protected String boardId;
    protected String boardName;
    protected List<WorkflowDto> workflows;
    protected List<BoardMemberDto> boardMembers;
    protected Map<String, List<CardDto>> committedCards;
    protected long boardVersion;

    public BoardContentViewModel() {

    }

    public BoardContentViewModel(String boardId, String boardName) {
        this.boardId = boardId;
        this.boardName = boardName;
        workflows = new ArrayList<>();
        boardMembers = new ArrayList<>();
        committedCards = new HashMap<>();
        boardVersion = 0;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public List<WorkflowDto> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<WorkflowDto> workflows) {
        this.workflows = workflows;
    }

    public List<BoardMemberDto> getBoardMembers() {
        return boardMembers;
    }

    public void setBoardMembers(List<BoardMemberDto> boardMembers) {
        this.boardMembers = boardMembers;
    }

    public Map<String, List<CardDto>> getCommittedCards() {
        return committedCards;
    }

    public void setCommittedCards(Map<String, List<CardDto>> committedCards) {
        this.committedCards = committedCards;
    }

    public long getBoardVersion() {
        return boardVersion;
    }

    public void setBoardVersion(long boardVersion) {
        this.boardVersion = boardVersion;
    }
}
