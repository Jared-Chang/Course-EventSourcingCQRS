package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create;

import ntut.csie.sslab.ddd.usecase.Input;

public class CreateBoardInput implements Input {
	private String name;
	private String userId;
	private String teamId;
	private String boardId;

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

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
}
