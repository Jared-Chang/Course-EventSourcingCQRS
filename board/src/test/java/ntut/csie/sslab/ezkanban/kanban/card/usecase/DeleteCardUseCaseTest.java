package ntut.csie.sslab.ezkanban.kanban.card.usecase;


import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;


public class DeleteCardUseCaseTest extends AbstractSpringBootJpaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void delete_a_card() {
        Card card = createCardUseCase();
        await().untilAsserted(() -> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(CardEvents.CardCreated.class)));
        DeleteCardUseCase deleteCardUseCase = newDeleteCardUseCase();
        DeleteCardInput input = new DeleteCardInput();
        input.setBoardId(boardId.id());
        input.setWorkflowId(workflowId);
        input.setLaneId(rootStageId);
        input.setCardId(card.getId().id());
        input.setUserId(userId);

        var output = deleteCardUseCase.execute(input);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertFalse(cardRepository.findById(CardId.valueOf(output.getId())).isPresent());
        await().untilAsserted(() -> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(CardEvents.CardDeleted.class)));
    }
}