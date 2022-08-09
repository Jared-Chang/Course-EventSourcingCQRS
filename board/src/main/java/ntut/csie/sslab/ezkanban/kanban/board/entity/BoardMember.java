package ntut.csie.sslab.ezkanban.kanban.board.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ntut.csie.sslab.ddd.entity.ValueObject;

public class BoardMember implements ValueObject {
	private BoardId boardId;
	private String userId;
	private BoardRole boardRole;

	@JsonCreator
	public BoardMember(
			@JsonProperty("boardRole") BoardRole boardRole,
			@JsonProperty("boardId") BoardId boardId,
			@JsonProperty("userId") String userId) {
		this.boardRole = boardRole;
		this.boardId = boardId;
		this.userId = userId;
	}

	public BoardId getBoardId() {
		return boardId;
	}

	public String getUserId() {
		return userId;
	}

	public BoardRole getBoardRole() {
		return boardRole;
	}

}
