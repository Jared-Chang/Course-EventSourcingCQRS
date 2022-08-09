package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="board_member")
public class BoardMemberData {
	@EmbeddedId
	private BoardMemberDataId boardMemberDataId;

	public BoardMemberData(BoardMemberDataId boardMemberDataId, String role) {
		this.boardMemberDataId = boardMemberDataId;
		this.role = role;
	}

	public BoardMemberData() {
	}

	@Column(name="board_role", nullable = false)
	private String role;

	public String getBoardId() {
		return boardMemberDataId.getBoardId();
	}

	public void setBoardId(String boardId) {
		boardMemberDataId.setBoardId(boardId);
	}

	public String getUserId() {
		return boardMemberDataId.getUserId();
	}

	public void setUserId(String userId) {
		boardMemberDataId.setUserId(userId);
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.boardMemberDataId.getUserId());
		hash = 79 * hash + Objects.hashCode(this.boardMemberDataId.getBoardId());
		hash = 79 * hash + Objects.hashCode(this.role);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BoardMemberData other = (BoardMemberData) obj;
		if (!Objects.equals(this.boardMemberDataId, other.boardMemberDataId)) {
			return false;
		}
		return Objects.equals(this.role, other.role);
	}
}