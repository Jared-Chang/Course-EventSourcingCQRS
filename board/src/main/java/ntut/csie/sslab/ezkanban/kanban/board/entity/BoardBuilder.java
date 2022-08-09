package ntut.csie.sslab.ezkanban.kanban.board.entity;

public class BoardBuilder {
	private String boardId;
	private String name;
	private String teamId;

	public static BoardBuilder newInstance() {
		return new BoardBuilder();
	}


	public BoardBuilder teamId(String teamId) {
		this.teamId = teamId;
		return this;
	}

	public BoardBuilder name(String name) {
		this.name = name;
		return this;
	}

	public BoardBuilder boardId(String boardId) {
		this.boardId = boardId;
		return this;
	}

	public Board build() {
		if(null == boardId) {
			throw new RuntimeException("Board id must be set.");
		}
		Board board = new Board(teamId, BoardId.valueOf(boardId), name);
		return board;
	}
}
