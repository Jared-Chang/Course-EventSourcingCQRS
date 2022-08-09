package ntut.csie.sslab.ezkanban.kanban.card.entity;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static ntut.csie.sslab.ddd.entity.common.Contract.ensure;
import static ntut.csie.sslab.ddd.entity.common.Contract.require;

public class Card extends AggregateRoot<CardId, DomainEvent> {

	public final static String CATEGORY = "Card";
	private String userId;
	private BoardId boardId;
	private WorkflowId workflowId;
	private LaneId laneId;
	private String description;

	public Card(List<? extends DomainEvent> domainEvents) {
		super(domainEvents);
	}

	public Card(BoardId boardId, WorkflowId workflowId, LaneId laneId, CardId cardId, String description, int expectedOrder, String userId) {
		super();

		apply(new CardEvents.CardCreated(
				boardId,
				workflowId,
				laneId,
				cardId,
				description,
				expectedOrder,
				userId,
				UUID.randomUUID(),
				DateProvider.now()));

		ensure(format("Card id '%s' equals aggregate id '%s'", getCardId(), getId()), ()-> getCardId().equals(getId()));
	}

	public void markAsDeleted(String userId) {
		require("Card is not deleted", () -> !isDeleted());

		apply(new CardEvents.CardDeleted(
				boardId,
				workflowId,
				laneId,
				getCardId(),
				userId,
				UUID.randomUUID(),
				DateProvider.now()));

		ensure("Card is marked as deleted", isDeleted());
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}

	public void changeDescription(String newDescription, String userId) {
		apply(new CardEvents.CardDescriptionChanged(
				boardId, workflowId, getCardId(), newDescription, userId, UUID.randomUUID(), DateProvider.now()));
	}

	public CardId getCardId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void move(WorkflowId workflowId, LaneId newLaneId, String userId, int newOrder) {
		LaneId oldLaneId = laneId;
		apply(new CardEvents.CardMoved(getBoardId(), workflowId, getCardId(), oldLaneId, newLaneId, newOrder, userId, UUID.randomUUID(), DateProvider.now()));
	}

	public BoardId getBoardId() {
		return boardId;
	}

	public WorkflowId getWorkflowId() {
		return workflowId;
	}

	public LaneId getLaneId() {
		return laneId;
	}

	public String getUserId() {
		return userId;
	}

	public CardId getId() {
		return getCardId();
	}


	@Override
	protected void when(DomainEvent domainEvent) {
		switch (domainEvent) {
			case CardEvents.CardCreated event -> {
				this.id = event.cardId();
				this.boardId = event.boardId();
				this.laneId = event.laneId();
				this.workflowId = event.workflowId();
				this.description = event.description();
				this.userId = event.userId();
			}
			case CardEvents.CardMoved event -> laneId = event.newLaneId();
			case CardEvents.CardDeleted event -> isDeleted = true;
			case CardEvents.CardDescriptionChanged event -> description = event.description();
			default -> {
			}
		}
	}

}

