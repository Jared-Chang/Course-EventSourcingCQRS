package ntut.csie.sslab.ezkanban.kanban.board.usecase.getboardcontent;

import ntut.csie.sslab.ddd.adapter.repository.IdempotentRepositoryPeer;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentStateRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardRole;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.*;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardMemberDto;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.NotifyBoardContentService;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardDto;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google.NotifyBoardContentAdapter;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneType;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneDto;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.*;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;


public class GetBoardContentUseCaseTest extends AbstractSpringBootJpaTest {

    private String firstWorkflowId;
    private String firstStageId;
    private String substage1Id;
    private String substage2Id;
    private String firstCardId;
    private String secondCardId;
    private String thirdCardId;
    private String fourthCardId;

    private String email;
    private String nickname;
    private String assignee1Id;
    private String assignee2Id;
    private List<String> assigneeIds;
    private String assignee1Nickname;
    private String assignee2Nickname;
    private String assignee1Email;
    private String assignee2Email;

    private NotifyBoardContentAdapter notifyBoardContentAdapter;

    private BoardContentStateRepository boardContentStateRepository;
    private BoardContentReadModelRepository boardContentReadModelRepository;

    @Autowired
    BoardContentReadModelRepositoryPeer boardContentReadModelRepositoryPeer;

    @Autowired
    IdempotentRepositoryPeer idempotentRepositoryPeer;

    @BeforeEach
    public void setUp() {
        super.setUp();
        boardContentStateRepository = new BoardContentStateRepositoryImpl(boardContentReadModelRepositoryPeer, idempotentRepositoryPeer);
        boardContentReadModelRepository = new BoardContentReadModelRepositoryImpl(boardContentReadModelRepositoryPeer, userRepository);

        notifyBoardContentAdapter = new NotifyBoardContentAdapter(new NotifyBoardContentService(UUID.randomUUID().toString(), boardContentStateRepository));
        executor.execute(notifyBoardContentAdapter);

        domainEventBus.register(notifyBoardContentAdapter);
    }

    @Test
    public void get_board_content_with_a_valid_board_id() {
        createThreeUsersInBoardBoundedContext();
        createBoardUseCase(teamId, boardId, boardName, userId);
        inviteThreeUsers();
        createOneWorkflowAndThreeStages();
        createFourCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(4)).when(isA(CardEvents.CardCreated.class)));
        moveTwoCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(2)).when(isA(CardEvents.CardMoved.class)));

        DateProvider.setDate(DateProvider.parse("2021-04-07 00:00:00"));
        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);

        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(boardId.id(), boardContentViewModel.getBoardId());
        assertEquals(boardName, boardContentViewModel.getBoardName());

        assertEquals(1, boardContentViewModel.getWorkflows().size());
        assertEquals(3 , boardContentViewModel.getBoardMembers().size());
        WorkflowDto firstWorkflowDto = boardContentViewModel.getWorkflows().get(0);
        assertEquals(firstWorkflowId, firstWorkflowDto.getWorkflowId());
        assertEquals(1, firstWorkflowDto.getLanes().size());
        LaneDto firstStageDto = firstWorkflowDto.getLanes().get(0);
        assertEquals(2, firstStageDto.getLanes().size());
        LaneDto substageDto = firstStageDto.getLanes().get(0);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage1Id, substageDto.getLaneId());
        substageDto = firstStageDto.getLanes().get(1);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage2Id, substageDto.getLaneId());
        Map<String, List<CardDto>> committedCards = boardContentViewModel.getCommittedCards();

        List<CardDto> cardDtosInFirstSubStage = committedCards.get(substage1Id);
        assertEquals(2, cardDtosInFirstSubStage.size());
        assertEquals(secondCardId, cardDtosInFirstSubStage.get(0).getCardId());
        assertEquals(fourthCardId, cardDtosInFirstSubStage.get(1).getCardId());

        List<CardDto> cardDtosInSecondSubStage = committedCards.get(substage2Id);
        assertEquals(2, cardDtosInSecondSubStage.size());
        assertEquals(firstCardId, cardDtosInSecondSubStage.get(0).getCardId());
        assertEquals(thirdCardId, cardDtosInSecondSubStage.get(1).getCardId());

        BoardMemberDto userDto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(userId)).findFirst().get();
        BoardMemberDto assignee1Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee1Id)).findFirst().get();
        BoardMemberDto assignee2Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee2Id)).findFirst().get();
        assertEquals(email, userDto.getEmail());
        assertEquals(nickname, userDto.getNickname());
        assertEquals(assignee1Email, assignee1Dto.getEmail());
        assertEquals(assignee1Nickname, assignee1Dto.getNickname());
        assertEquals(assignee2Email, assignee2Dto.getEmail());
        assertEquals(assignee2Nickname, assignee2Dto.getNickname());
    }


    @Test
    public void get_board_content_with_an_invalid_board_id() {
        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);

        assertEquals(ExitCode.FAILURE, output.getExitCode());
        assertEquals("Get board failed: board not found, board id = " + boardId.id(), output.getMessage());
    }

    @Test
    public void get_board_content_is_idempotent_when_a_board_created_and_a_workflow_created_are_sent_twice() {

        DuplicatedEventsPublisher publisher = new DuplicatedEventsPublisher(domainEventBus);
        var myExecutor = new SimpleAsyncTaskExecutor();
        myExecutor.execute(publisher);
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(2)).when(isA(WorkflowEvents.WorkflowCreated.class)));

        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);
        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(1, boardContentViewModel.getWorkflows().size());

    }

    private void createThreeUsersInBoardBoundedContext(){
        email = "userEmail";
        nickname = "userNickname";
        assignee1Id = "assignee1Id";
        assignee2Id = "assignee2Id";
        assigneeIds = Arrays.asList(assignee1Id, assignee2Id);
        assignee1Nickname = "assignee1Nickname";
        assignee2Nickname = "assignee2Nickname";
        assignee1Email = "assignee1Email";
        assignee2Email = "assignee2Email";
        UserDto user1Dto = new UserDto(assignee1Id, assignee1Email, assignee1Nickname);
        UserDto user2Dto = new UserDto(assignee2Id, assignee2Email, assignee2Nickname);
        userRepository.save(new UserDto(userId, email, nickname));
        userRepository.save(user1Dto);
        userRepository.save(user2Dto);
    }

    private void inviteThreeUsers(){
        Board board = boardRepository.findById(boardId).get();
        board.joinAs(BoardRole.Admin, userId);
        board.joinAs(BoardRole.Admin, assignee1Id);
        board.joinAs(BoardRole.Member, assignee2Id);
        boardRepository.save(board);
    }

    private void createOneWorkflowAndThreeStages(){
        firstWorkflowId = createWorkflowUseCase(boardId, "firstWorkflow", userId);

        firstStageId = createStageUseCase(firstWorkflowId, "-1", "firstStage", -1, LaneType.Standard.toString(), userId, boardId
        );
        substage1Id = createStageUseCase(firstWorkflowId, firstStageId, "substage1", -1, LaneType.Standard.toString(), userId, boardId
        );
        substage2Id = createStageUseCase(firstWorkflowId, firstStageId, "substage2", -1, LaneType.Standard.toString(), userId, boardId
        );
    }

    private void createFourCards(){
        DateProvider.setDate(DateProvider.parse("2021-04-01 00:00:00"));
        firstCardId = createCardUseCase(boardId, firstWorkflowId, substage1Id, "firstCard", "xl", "notes", DateProvider.now(),
                userId).getCardId().id();
        secondCardId = createCardUseCase(boardId, firstWorkflowId, substage1Id, "secondCard", "xl", "notes", DateProvider.now(),
                userId).getCardId().id();
        thirdCardId = createCardUseCase(boardId, firstWorkflowId, substage1Id, "thirdCard", "xl", "notes", DateProvider.now(),
                userId).getCardId().id();
        fourthCardId = createCardUseCase(boardId, firstWorkflowId, substage1Id, "fourthCard", "xl", "notes", DateProvider.now(),
                userId).getCardId().id();
    }

    private void moveTwoCards() {
        DateProvider.setDate(DateProvider.parse("2021-04-05 00:00:00"));
        moveCardUseCase(boardId, firstCardId, firstWorkflowId, substage1Id, substage2Id, 0, userId);
        moveCardUseCase(boardId, thirdCardId, firstWorkflowId, substage1Id, substage2Id, 1, userId);
    }


    class DuplicatedEventsPublisher implements Runnable{
        final private List<DomainEvent> events;
        final DomainEventBus eventBus;
        public DuplicatedEventsPublisher(DomainEventBus eventBus){
            this.eventBus = eventBus;
            events = new ArrayList<>();
            var boardCreated = new BoardEvents.BoardCreated(teamId, boardId, boardName, UUID.randomUUID(), DateProvider.now());
            var workflowCreated = new WorkflowEvents.WorkflowCreated(boardId, WorkflowId.valueOf(workflowId), "dev", userId, UUID.randomUUID(), DateProvider.now());
            events.add(boardCreated);
            events.add(boardCreated);
            events.add(workflowCreated);
            events.add(workflowCreated);
        }

        @Override
        public void run() {
            while (!events.isEmpty()) {
                try {
                    DomainEvent event = events.get(0);
                    eventBus.post(event);
                    events.remove(0);
                    Thread.sleep(100);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
}
