package ntut.csie.sslab.ezkanban.kanban.workflow.usecase;

import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;

public class CreateWorkflowUseCaseTest extends AbstractSpringBootJpaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void create_a_workflow() {

        BoardId boardId = BoardId.create();
        CreateWorkflowUseCase createWorkflowUseCase = newCreateWorkflowUseCase();
        CreateWorkflowInput createWorkflowInput = new CreateWorkflowInput();
        createWorkflowInput.setName("workflow");
        createWorkflowInput.setBoardId(boardId.id());
        createWorkflowInput.setUserId(userId);

        var output = createWorkflowUseCase.execute(createWorkflowInput);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertCreatedWorkflowFromRepository(output.getId(), boardId);
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(WorkflowEvents.WorkflowCreated.class)));
    }

    private BoardId createBoard(){
        String teamId = UUID.randomUUID().toString();
        BoardId boardId = BoardId.create();
        String boardName = "DevOps board";
        String userId = "user_id_888";
        super.createBoardUseCase(teamId, boardId, boardName, userId);
        return boardId;
    }

    private void assertCreatedWorkflowFromRepository(String workflowId, BoardId boardId){
        Workflow workflow = workflowRepository.findById(WorkflowId.valueOf(workflowId)).get();
        assertEquals("workflow", workflow.getName());
        Assertions.assertEquals(boardId, workflow.getBoardId());
        assertEquals(0, workflow.getRootStages().size());
    }
}
