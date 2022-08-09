package ntut.csie.sslab.ezkanban.kanban.card.adapter.out.repository.springboot;

import ntut.csie.sslab.ddd.usecase.RepositorySaveException;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CardRepositoryImplTest extends AbstractSpringBootJpaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    private Card createCardInstance(){
        return new Card(
                BoardId.create(),
                WorkflowId.create(),
                LaneId.create(),
                CardId.create(),
                "description",
                0,
                userId);
    }

    @Test
    public void version_starts_from_zero_and_increases_by_the_number_of_domain_events(){

        Card card = createCardInstance();
        cardRepository.save(card);
        card.clearDomainEvents();
        assertEquals(0, card.getVersion());
        card.changeDescription("d1", userId);
        card.changeDescription("d2", userId);
        card.changeDescription("d3", userId);

        cardRepository.save(card);
        card.clearDomainEvents();
        assertEquals(3, card.getVersion());

        card.changeDescription("d4", userId);
        card.changeDescription("d5", userId);
        card.changeDescription("d6", userId);
        cardRepository.save(card);
        card.clearDomainEvents();
        assertEquals(6, card.getVersion());
    }


    @Test
    @EnabledIf(expression = "#{environment['ezkanban.datasource'] == 'RDB'}", loadContext = true)
    public void optimistic_locking_failure_will_rollback_when_update_existing_card_using_postgres(){

        final Card card = createCardInstance();
        cardRepository.save(card);
        assertEquals(0, card.getVersion());
        RepositorySaveException thrown =
                assertThrows(RepositorySaveException.class, () -> {
                    card.changeDescription("new description", userId);
                    long incorrectVersion = card.getVersion() + card.getDomainEventSize() + 1;
                    card.setVersion(incorrectVersion);
                    cardRepository.save(card);
                }, "optimistic locking failure caused by incorrect version number");
        assertEquals("Optimistic locking failure", thrown.getMessage());

        Card cardFromDB = cardRepository.findById(card.getCardId()).get();
        assertEquals(0, cardFromDB.getVersion());
        assertEquals("description", cardFromDB.getDescription());

        // TODO: verify that domain events have not been saved
    }

    @Test
    @EnabledIf(expression = "#{environment['ezkanban.datasource'] == 'ESDB'}", loadContext = true)
    public void optimistic_locking_failure_will_rollback_when_update_existing_card_using_ESDB(){

        final Card card = createCardInstance();
        cardRepository.save(card);
        assertEquals(0, card.getVersion());
        RuntimeException thrown =
                assertThrows(RuntimeException.class, () -> {
                    card.changeDescription("new description", userId);
                    List<String> assigneeIds = Arrays.asList("Assignee1Id", "Assignee2Id", "Assignee3Id");
                    long incorrectVersion = card.getVersion() + card.getDomainEventSize() + 1;
                    card.setVersion(incorrectVersion);
                    cardRepository.save(card);
                }, "optimistic locking failure caused by incorrect version number");
        assertEquals("Optimistic locking failure", thrown.getMessage());

        Card cardFromDB = cardRepository.findById(card.getCardId()).get();
        assertEquals(0, cardFromDB.getVersion());
        assertEquals("description", cardFromDB.getDescription());

        // TODO: verify that domain events have not been saved
    }

    @Test
    @EnabledIf(expression = "#{environment['ezkanban.datasource'] == 'ESDB'}", loadContext = true)
    public void optimistic_locking_failure_will_rollback_when_save_new_card_using_ESDB(){

        final Card card = createCardInstance();
        // TODO: the exception type is not clear
        RuntimeException thrown =
                assertThrows(RuntimeException.class, () -> {
                    card.changeDescription("not empty description", userId);
                    long incorrectVersion = card.getVersion() + card.getDomainEventSize() + 1;
                    card.setVersion(incorrectVersion);
                    cardRepository.save(card);
                }, "optimistic locking failure caused by incorrect version number");
        assertEquals("Optimistic locking failure", thrown.getMessage());

        assertFalse(cardRepository.findById(card.getCardId()).isPresent());
    }

}
