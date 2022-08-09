package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move;

import ntut.csie.sslab.ddd.usecase.VersionedInput;

public class MoveCardInput implements VersionedInput {

	private String workflowId;
	private String cardId;
	private String oldLaneId;
	private String newLaneId;
	private int order;
	private String userId;
	private String boardId;
	private long version;

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getOldLaneId() {
		return oldLaneId;
	}

	public void setOldLaneId(String oldLaneId) {
		this.oldLaneId = oldLaneId;
	}

	public String getNewLaneId() {
		return newLaneId;
	}

	public void setNewLaneId(String newLaneId) {
		this.newLaneId = newLaneId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	@Override
	public long getVersion() {
		return version;
	}

	@Override
	public void setVersion(long version) {
		this.version = version;
	}
}

