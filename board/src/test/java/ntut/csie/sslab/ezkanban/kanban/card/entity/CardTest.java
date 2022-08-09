package ntut.csie.sslab.ezkanban.kanban.card.entity;


import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CardTest {

    private String userId = "userId";

    private Card createCard(){
        return new Card(BoardId.create(), WorkflowId.create(), LaneId.create(), CardId.create(), "description",  0, userId);
    }

    @Test
    public void create_a_valid_card() {
        CardId cardId = CardId.create();
        BoardId boardId = BoardId.create();
        LaneId laneId = LaneId.create();
        WorkflowId workflowId = WorkflowId.create();
        String description = "description";

        Card card = new Card(boardId,
                workflowId,
                laneId,
                cardId,
                description,
                0,
                userId);

        assertEquals(boardId, card.getBoardId());
        assertEquals(workflowId, card.getWorkflowId());
        assertEquals(laneId, card.getLaneId());
        assertEquals(cardId, card.getCardId());
        assertEquals(description, card.getDescription());
        assertEquals(userId, card.getUserId());
        assertFalse(card.isDeleted());
    }

    @Test
    public void delete_a_card_marks_it_as_deleted() {
        Card card = createCard();

        card.markAsDeleted(userId);

        assertTrue(card.isDeleted());
    }


    @Test
    public void change_card_description_with_a_new_value() {
        Card card = createCard();

        card.changeDescription("newDescription", userId);

        assertEquals("newDescription", card.getDescription());
    }
}
