package ntut.csie.sslab.ezkanban.kanban.card.usecase;


import ntut.csie.sslab.ddd.adapter.presenter.cqrs.CqrsCommandPresenter;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;

public class ChangeCardDescriptionUseCaseTest extends AbstractSpringBootJpaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void change_a_card_description() {
        String firstCardId = createCardUseCase().getCardId().id();
        ChangeCardDescriptionUseCase changeCardDescriptionUseCase = newChangeCardDescriptionUseCase();
        ChangeCardDescriptionInput input = new ChangeCardDescriptionInput();
        input.setCardId(firstCardId);
        input.setDescription("EditedDescription");
        input.setBoardId(boardId.id());
        input.setUserId(userId);

        var output = changeCardDescriptionUseCase.execute(input);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        Card card = cardRepository.findById(CardId.valueOf(output.getId())).get();
        assertEquals(input.getDescription(), card.getDescription());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(CardEvents.CardDescriptionChanged.class)));
    }
}