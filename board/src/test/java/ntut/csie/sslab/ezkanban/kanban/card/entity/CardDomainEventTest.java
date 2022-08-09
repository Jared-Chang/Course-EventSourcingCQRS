package ntut.csie.sslab.ezkanban.kanban.card.entity;


import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CardDomainEventTest {

    private String userId = "userId";

    private Card createCard(){
        return new Card(BoardId.valueOf("boardId"),
                WorkflowId.valueOf("workflowId"),
                LaneId.valueOf("laneId"),
                CardId.create(),
                "description",
                0,
                userId);
    }

    private Card createCardAndClearDomainEvent(){
        Card card = createCard();
        card.clearDomainEvents();
        return card;
    }


    @Test
    public void create_a_card_generates_a_card_created_domain_event() {

        Card card = createCard();

        assertEquals(1, card.getDomainEvents().size());
        CardEvents.CardCreated cardCreated = (CardEvents.CardCreated) card.getDomainEvents().get(0);
        assertEquals(card.getBoardId(), cardCreated.boardId());
        assertEquals(card.getWorkflowId(), cardCreated.workflowId());
        assertEquals(card.getLaneId(), cardCreated.laneId());
        assertEquals(card.getCardId(), cardCreated.cardId());
        assertEquals(card.getDescription(), cardCreated.description());
        assertEquals(userId, cardCreated.userId());
    }

    @Test
    public void delete_a_card_generates_a_card_deleted_domain_event() {
        Card card = createCardAndClearDomainEvent();

        card.markAsDeleted(userId);

        assertEquals(1, card.getDomainEvents().size());
        CardEvents.CardDeleted cardDeleted = (CardEvents.CardDeleted) card.getDomainEvents().get(0);
        assertEquals(card.getBoardId(), cardDeleted.boardId());
        assertEquals(card.getWorkflowId(), cardDeleted.workflowId());
        assertEquals(card.getLaneId(), cardDeleted.laneId());
        assertEquals(card.getCardId(), cardDeleted.cardId());
        assertEquals(userId, cardDeleted.userId());
    }

    @Test
    public void change_a_card_description_generates_a_card_description_changed_domain_event() {
        Card card = createCardAndClearDomainEvent();

        card.changeDescription("newDescription", userId);

        assertEquals(1, card.getDomainEvents().size());
        CardEvents.CardDescriptionChanged cardDescriptionChanged = (CardEvents.CardDescriptionChanged) card.getDomainEvents().get(0);
        assertEquals(card.getBoardId(), cardDescriptionChanged.boardId());
        assertEquals(card.getCardId(), cardDescriptionChanged.cardId());
        assertEquals(card.getDescription(), cardDescriptionChanged.description());
        assertEquals(userId, cardDescriptionChanged.userId());
    }
}
