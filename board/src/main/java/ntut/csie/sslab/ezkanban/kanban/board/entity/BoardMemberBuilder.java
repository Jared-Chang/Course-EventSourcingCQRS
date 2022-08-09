package ntut.csie.sslab.ezkanban.kanban.board.entity;


public class BoardMemberBuilder {
	private BoardId boardId;
	private String userId;
	private BoardRole memberType;
	
	public static BoardMemberBuilder newInstance() {
		return new BoardMemberBuilder();
	}
	
	public BoardMemberBuilder boardId(BoardId boardId) {
		this.boardId = boardId;
		return this;
	}
	
	public BoardMemberBuilder userId(String userId) {
		this.userId = userId;
		return this;
	}

	public BoardMemberBuilder memberType(BoardRole memberType) {
		this.memberType = memberType;
		return this;
	}

	public BoardMember build() {
		BoardMember boardMember = new BoardMember(memberType ,boardId, userId);
		return boardMember;
	}
}
