package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository;

import java.util.Set;

public class CardDto {
	private String cardId;
	private String boardId;
	private String workflowId;
	private String laneId;
	private String description;
	private String userId;
	private long version;

	public String getCardId() {
		return cardId;
	}
	
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getLaneId() {
		return laneId;
	}
	
	public void setLaneId(String laneId) {
		this.laneId = laneId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
