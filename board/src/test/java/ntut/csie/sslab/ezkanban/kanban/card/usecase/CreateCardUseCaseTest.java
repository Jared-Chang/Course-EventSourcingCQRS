package ntut.csie.sslab.ezkanban.kanban.card.usecase;


import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.DateFormat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;


public class CreateCardUseCaseTest extends AbstractSpringBootJpaTest {


    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void create_a_card_in_a_root_stage() {
        DateFormat df = DateFormat.getDateInstance();

        CreateCardUseCase createCardUseCase = newCreateCardUseCase();
        CreateCardInput input = new CreateCardInput();
        input.setWorkflowId(workflowId);
        input.setLaneId(rootStageId);
        input.setDescription("firstCard");
        input.setEstimate("xl");
        input.setNote("firstNotes");
        input.setDeadline(DateProvider.parse("2021-01-01 00:00:00"));
        input.setUserId(userId);
        input.setBoardId(boardId.id());
        input.setExpectedOrder(0);

        var output = createCardUseCase.execute(input);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        Card card = cardRepository.findById(CardId.valueOf(output.getId())).get();
        assertEquals(input.getUserId(), card.getUserId());
        assertEquals(input.getBoardId(), card.getBoardId().id());
        assertEquals(input.getLaneId(), card.getLaneId().id());
        assertEquals(input.getDescription(), card.getDescription());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(CardEvents.CardCreated.class)));
    }
}