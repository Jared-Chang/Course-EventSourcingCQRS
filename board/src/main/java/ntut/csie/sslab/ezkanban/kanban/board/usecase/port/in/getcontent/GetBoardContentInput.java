package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ddd.usecase.Input;

public class GetBoardContentInput implements Input {
    private String boardId;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
}
