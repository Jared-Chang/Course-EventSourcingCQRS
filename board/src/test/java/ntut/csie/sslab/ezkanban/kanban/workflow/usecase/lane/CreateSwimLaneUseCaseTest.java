//package ntut.csie.sslab.kanban.usecase.lane;
//
//import ntut.csie.sslab.ddd.adapter.presenter.cqrs.CqrsCommandPresenter;
//import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
//import ntut.csie.sslab.kanban.entity.model.workflow.*;
//import ntut.csie.sslab.kanban.usecase.AbstractSpringBootJpaTest;
//import ntut.csie.sslab.kanban.usecase.lane.swimLane.create.CreateSwimLaneInput;
//import ntut.csie.sslab.kanban.usecase.lane.swimLane.create.CreateSwimLaneUseCase;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import static org.awaitility.Awaitility.await;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.isA;
//
//
//public class CreateSwimLaneUseCaseTest extends AbstractSpringBootJpaTest {
//
//    @BeforeEach
//    public void setUp() {
//        super.setUp();
//    }
//
//    @Test
//    public void create_a_swimLane() {
//        createBoardUseCase(teamId, boardId,"board name", userId);
//        String workflowId = createWorkflowUseCase(boardId, "workflow name", userId);
//        String todoStageId = createRootStageUseCase(boardId, workflowId);
//        CreateSwimLaneUseCase createSwimLaneUseCase = newCreateSwimLaneUseCase();
//        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(WorkflowEvents.StageCreated.class)));
//        CqrsCommandPresenter output = CqrsCommandPresenter.newInstance();
//        CreateSwimLaneInput input = createSwimLaneUseCase.newInput();
//        input.setWorkflowId(workflowId);
//        input.setParentId(todoStageId);
//        input.setName("SwimlaneName");
//        input.setWipLimit(WipLimit.UNLIMIT.value());
//        input.setLaneType(LaneType.Standard.name());
//        input.setUserId(userId);
//        input.setBoardId(boardId.id());
//        input.setVersion(workflowRepository.findById(WorkflowId.valueOf(workflowId)).get().getVersion());
//
//        createSwimLaneUseCase.execute(input, output);
//
//        Workflow workflow = workflowRepository.findById(WorkflowId.valueOf(workflowId)).get();
//        assertNotNull(output.getId());
//        assertEquals(ExitCode.SUCCESS, output.getExitCode());
//        assertEquals(1, workflow.getRootStages().size());
//        assertEquals(1, workflow.getRootStages().get(0).getChildren().size());
//        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(WorkflowEvents.SwimLaneCreated.class)));
//    }
//}
