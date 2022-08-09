package ntut.csie.sslab.ezkanban.kanban.card.entity;

import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.time.Instant;

public class CardBuilder {
	private String workflowId;
	private String cardId;
	private String laneId;
	private String userId;
	private String description;
	private String boardId;
	private int expectedOrder = -1;
	
	public static CardBuilder newInstance() {
		return new CardBuilder();
	}

	public CardBuilder cardId(String cardId) {
		this.cardId = cardId;
		return this;
	}

	public CardBuilder workflowId(String workflowId) {
		this.workflowId = workflowId;
		return this;
	}

	public CardBuilder workflowId(WorkflowId workflowId) {
		this.workflowId = workflowId.id();
		return this;
	}


	public CardBuilder laneId(String laneId) {
		this.laneId = laneId;
		return this;
	}

	public CardBuilder laneId(LaneId laneId) {
		this.laneId = laneId.id();
		return this;
	}

	public CardBuilder userId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public CardBuilder description(String description) {
		this.description = description;
		return this;
	}
	
	public CardBuilder boardId(String boardId) {
		this.boardId = boardId;
		return this;
	}

	public CardBuilder boardId(BoardId boardId) {
		this.boardId = boardId.id();
		return this;
	}

	public CardBuilder expectedOrder(int expectedOrder) {
		this.expectedOrder = expectedOrder;
		return this;
	}

	public Card build() {
		if(null == cardId) {
			throw new RuntimeException("Card id must be set.");
		}
		if(null == boardId) {
			throw new RuntimeException("Board id must be set.");
		}

		return new Card(BoardId.valueOf(boardId), WorkflowId.valueOf(workflowId), LaneId.valueOf(laneId), CardId.valueOf(cardId), description, expectedOrder, userId);
	}
}
