package ntut.csie.sslab.ezkanban.kanban.card.usecase;

import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.isA;

public class MoveCardUseCaseTest extends AbstractSpringBootJpaTest {


    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void move_a_card_from_todo_stage_to_doing_stage() {
        LaneId todoStageId = LaneId.valueOf("todo Stage Id");
        LaneId doingStageId = LaneId.valueOf("doing Stage Id");
        Card firstCard = createCardUseCase();
        MoveCardUseCase moveCardUseCase = newMoveCardUseCase();
        MoveCardInput input = new MoveCardInput();
        input.setCardId(firstCard.getCardId().id());
        input.setWorkflowId(workflowId);
        input.setOldLaneId(todoStageId.id());
        input.setNewLaneId(doingStageId.id());
        input.setOrder(0);
        input.setUserId(userId);
        input.setBoardId(boardId.id());

        var output = moveCardUseCase.execute(input);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS,output.getExitCode());
        Card card = cardRepository.findById(firstCard.getCardId()).get();
        assertEquals(input.getNewLaneId(), card.getLaneId().id());
        await().untilAsserted(() -> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(CardEvents.CardMoved.class)));
    }

    @Test
    public void move_a_card_fail_when_version_not_match() {
        assumeTrue(!dataSourceConfig.getDataSource().equalsIgnoreCase("MEM"));

        LaneId todoStageId = LaneId.valueOf("todo Stage Id");
        LaneId doingStageId = LaneId.valueOf("doing Stage Id");
        Card firstCard = createCardUseCase();
        MoveCardUseCase moveCardUseCase = newMoveCardUseCase();
        MoveCardInput input = new MoveCardInput();
        input.setCardId(firstCard.getCardId().id());
        input.setWorkflowId(workflowId);
        input.setOldLaneId(todoStageId.id());
        input.setNewLaneId(doingStageId.id());
        input.setOrder(0);
        input.setUserId(userId);
        input.setBoardId(boardId.id());
        input.setVersion(100l);

        assertThrows(UseCaseFailureException.class, () -> { moveCardUseCase.execute(input); });
    }
}