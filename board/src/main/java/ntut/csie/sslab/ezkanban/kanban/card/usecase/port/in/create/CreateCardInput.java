package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create;

import ntut.csie.sslab.ddd.usecase.Input;

import java.util.Date;

public class CreateCardInput implements Input{
	private String workflowId;
	private String laneId;
	private String userId;
	private String description;
	private String estimate;
	private String note;
	private Date deadline;
	private String type;
	private String boardId;
	private int expectedOrder;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEstimate() {
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public int getExpectedOrder() {
		return expectedOrder;
	}

	public void setExpectedOrder(int expectedOrder) {
		this.expectedOrder = expectedOrder;
	}
}
