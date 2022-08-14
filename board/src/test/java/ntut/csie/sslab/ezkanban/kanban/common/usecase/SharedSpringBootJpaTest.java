package ntut.csie.sslab.ezkanban.kanban.common.usecase;

import com.eventstore.dbclient.EventStoreDBProjectionManagementClient;
import com.eventstore.dbclient.UpdateProjectionOptions;
import ntut.csie.sslab.ddd.adapter.eventbroker.EsdbListener;
import ntut.csie.sslab.ddd.adapter.eventbroker.EsdbPersistentListener;
import ntut.csie.sslab.ddd.adapter.eventbroker.PostgresDomainEventListener;
import ntut.csie.sslab.ddd.adapter.gateway.GoogleEventBusAdapter;
import ntut.csie.sslab.ddd.adapter.presenter.cqrs.CqrsCommandPresenter;
import ntut.csie.sslab.ddd.adapter.repository.EsdbStoreAdapter;
import ntut.csie.sslab.ddd.adapter.repository.EzOutboxStoreAdapter;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.framework.EzOutboxStore;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google.NotifyBoardAdapter;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardInput;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardUseCase;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardData;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.CreateBoardService;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.NotifyBoardService;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionUseCase;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardData;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.service.ChangeCardDescriptionService;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.service.CreateCardService;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.service.DeleteCardService;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.service.MoveCardUseService;
import ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config.DataSourceConfig;
import ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot.UserInBoardRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot.UserRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneType;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.NullLane;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WipLimit;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.stage.CreateStageInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.stage.CreateStageUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane.CreateSwimLaneInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane.CreateSwimLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.rename.RenameLaneInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.rename.RenameLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowData;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateStageService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateSwimLaneService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateWorkflowUseCaseService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.RenameLaneService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class SharedSpringBootJpaTest {
    @Value("${jdbc.test.url}")
    private String JDBC_TEST_URL;

    @Value("${spring.datasource.kanban.url}")
    public String url;

    @Value("${esdb.url}")
    protected String ESDB_URL;

    public static final long WAITING = 1000;

    public BoardRepository boardRepository;
    public WorkflowRepository workflowRepository;

    public CardRepository cardRepository;
    public UserRepository userRepository;
    public GoogleEventBusAdapter domainEventBus;

    public NotifyBoardAdapter notifyBoardAdapter;
    public AllEventsListener allEventsListener;
    public PostgresDomainEventListener postgresDomainEventListener;

    //    public ExecutorService executor;
    public TaskExecutor executor;

    public String teamId;
    public String teamName;
    public BoardId boardId;
    public String workflowId;
    public String rootStageId;
    public String laneId;
    public String tagId;
    public String userId;
    public String username;
    public String email;
    public String nickname;
    public String boardName;


    @Autowired
    public DomainEventTypeMapper domainEventTypeMapper;

    @Autowired
    public DataSourceConfig dataSourceConfig;


    @Autowired
    public UserInBoardRepositoryPeer userInBoardRepositoryPeer;

    @Autowired
    @Qualifier("BoardPostgresOutboxStoreClient")
    private EzOutboxStore boardEzOutboxStore;

    @Autowired
    @Qualifier("WorkflowPostgresOutboxStoreClient")
    private EzOutboxStore workflowEzOutboxStore;

    @Autowired
    @Qualifier("CardPostgresOutboxStoreClient")
    private EzOutboxStore cardEzOutboxStore;


    protected EventStore eventStore;


    public EsdbListener esdbListener;

    @BeforeEach
    public void setUp() {
        DateProvider.resetDate();

        System.out.println("Using database : " + dataSourceConfig.getDataSource());
        if (dataSourceConfig.getDataSource().equalsIgnoreCase("RDB"))
            System.out.println("url: " + url);
        else if (dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
            System.out.println("url: " + ESDB_URL);
        }

        executor = new SimpleAsyncTaskExecutor();

        userRepository = new UserRepositoryImpl(userInBoardRepositoryPeer);
        domainEventBus = new GoogleEventBusAdapter();

        if(dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {

            eventStore = new EsdbStoreAdapter(ESDB_URL);
            workflowRepository = new WorkflowEventSourcingRepository(eventStore);
            cardRepository = new CardEventSourcingRepository(eventStore);
            boardRepository = new BoardEventSourcingRepository(eventStore);
            esdbListener = new EsdbPersistentListener(ESDB_URL, domainEventTypeMapper, domainEventBus);
        }
        else if(dataSourceConfig.getDataSource().equalsIgnoreCase("RDB")) {

            boardRepository = new BoardOutboxRepository(new EzOutboxStoreAdapter<BoardData, String>(boardEzOutboxStore));
            workflowRepository = new WorkflowOutboxRepository(new EzOutboxStoreAdapter<WorkflowData, String>(workflowEzOutboxStore));
            cardRepository = new CardOutboxRepository(new EzOutboxStoreAdapter<CardData, String>(cardEzOutboxStore));
            try {
                postgresDomainEventListener = new PostgresDomainEventListener(JDBC_TEST_URL, "postgres", "root", 20, domainEventTypeMapper, domainEventBus);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        teamId = UUID.randomUUID().toString();
        teamName = "ntut team";
        boardId = BoardId.create();
        workflowId = UUID.randomUUID().toString();
        laneId = UUID.randomUUID().toString();
        tagId = UUID.randomUUID().toString();
        rootStageId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();
        username = "Teddy";
        email = "Teddy@gmail.com";
        nickname = "Teddy";
        boardName = "Task Board";

        if (dataSourceConfig.getDataSource().equalsIgnoreCase("RDB")) {
            executor.execute(postgresDomainEventListener);
        }
        else if (dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
            executor.execute(esdbListener);
        }

        notifyBoardAdapter = new NotifyBoardAdapter(new NotifyBoardService(boardRepository, domainEventBus));

        executor.execute(domainEventBus);
        executor.execute(notifyBoardAdapter);

        allEventsListener = Mockito.mock(AllEventsListener.class);
        domainEventBus.register(allEventsListener);
        DomainEventMapper.setMapper(domainEventTypeMapper);
    }

    @AfterEach
    public void teardown(){
        boardRepository.close();
        workflowRepository.close();
        cardRepository.close();
        userRepository.close();

        if (dataSourceConfig.getDataSource().equalsIgnoreCase("RDB"))
            postgresDomainEventListener.shutdown();
        else if (dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
            ((EsdbPersistentListener)esdbListener).deletePersistentSubscription();
            esdbListener.shutdown();
        }
    }

    @AfterAll
    public static void afterAll() {
    }

    public CreateBoardUseCase newCreateBoardUseCase (){
        return new CreateBoardService(boardRepository, domainEventBus);
    }

    public CreateWorkflowUseCase newCreateWorkflowUseCase() {
        return new CreateWorkflowUseCaseService(workflowRepository);
    }

    public CreateStageUseCase newCreateStageUseCase() {
        return new CreateStageService(workflowRepository, domainEventBus);
    }

    public RenameLaneUseCase newRenameLaneUseCase() {
        return new RenameLaneService(workflowRepository, domainEventBus);
    }

    public CreateSwimLaneUseCase newCreateSwimLaneUseCase() {
        return new CreateSwimLaneService(workflowRepository, domainEventBus);
    }

    public CreateCardUseCase newCreateCardUseCase() {
        return new CreateCardService(cardRepository, domainEventBus);
    }

    public DeleteCardUseCase newDeleteCardUseCase() {
        return new DeleteCardService(cardRepository, domainEventBus);
    }

    public MoveCardUseCase newMoveCardUseCase() {
        return new MoveCardUseService(cardRepository, domainEventBus);
    }

    public ChangeCardDescriptionUseCase newChangeCardDescriptionUseCase() {
        return new ChangeCardDescriptionService(cardRepository, domainEventBus);
    }

    public void createBoardUseCase(String teamId, BoardId boardId, String name, String userId){
        CreateBoardUseCase createBoardUseCase = newCreateBoardUseCase();

        CreateBoardInput input = new CreateBoardInput();
        input.setTeamId(teamId);
        input.setName(name);
        input.setUserId(userId);
        input.setBoardId(boardId.id());

        createBoardUseCase.execute(input);
    }

    public String createWorkflowUseCase(BoardId boardId, String workflowName, String userId) {
        CreateWorkflowUseCase createWorkflowUseCase = newCreateWorkflowUseCase();

        CreateWorkflowInput input = new CreateWorkflowInput();
        input.setName(workflowName);
        input.setBoardId(boardId.id());
        input.setUserId(userId);

        return createWorkflowUseCase.execute(input).getId();
    }

    public String createStageUseCase(
            String workflowId, String parentId, String name, int wipLimit, String laneType, String userId, BoardId boardId) {
        var workflow = workflowRepository.findById(WorkflowId.valueOf(workflowId)).get();
        CreateStageUseCase createStageUseCase = newCreateStageUseCase();

        CreateStageInput input = new CreateStageInput();
        input.setBoardId(boardId.id());
        input.setWorkflowId(workflowId);
        input.setParentId(parentId);
        input.setName(name);
        input.setWipLimit(wipLimit);
        input.setLaneType(laneType);
        input.setUserId(userId);
        input.setVersion(workflow.getVersion());

        CqrsCommandPresenter output = CqrsCommandPresenter.newInstance();

        return createStageUseCase.execute(input).getId();
    }
    public String createSwimlaneUseCase(
            String workflowId,
            String parentId,
            String name,
            int wipLimit,
            String laneType,
            String userId,
            BoardId boardId) {

        CreateSwimLaneUseCase createSwimLaneUseCase = newCreateSwimLaneUseCase();

        CreateSwimLaneInput input = new CreateSwimLaneInput();
        input.setWorkflowId(workflowId);
        input.setParentId(parentId);
        input.setName(name);
        input.setWipLimit(wipLimit);
        input.setLaneType(laneType);
        input.setUserId(userId);
        input.setBoardId(boardId.id());
        input.setVersion(workflowRepository.findById(WorkflowId.valueOf(workflowId)).get().getVersion());

        CqrsCommandPresenter output = CqrsCommandPresenter.newInstance();

        return createSwimLaneUseCase.execute(input).getId();
    }

    public String renameLaneUseCase(String workflowId, String laneId, String newName, String userId){

        RenameLaneUseCase renameLaneUseCase = newRenameLaneUseCase();
        RenameLaneInput input = new RenameLaneInput();
        input.setWorkflowId(workflowId);
        input.setLaneId(laneId);
        input.setNewName(newName);
        input.setUserId(userId);
        input.setVersion(workflowRepository.findById(WorkflowId.valueOf(workflowId)).get().getVersion());
        CqrsCommandPresenter output = CqrsCommandPresenter.newInstance();

        return  renameLaneUseCase.execute(input).getId();
    }

    public Card createCardUseCase(BoardId boardId, String workflowId, String laneId, String description, String estimate, String note, Date deadline, String userId){

        CreateCardUseCase createCardUseCase = newCreateCardUseCase();
        CreateCardInput createCardInput = new CreateCardInput();
        createCardInput.setWorkflowId(workflowId);
        createCardInput.setLaneId(laneId);
        createCardInput.setUserId(userId);
        createCardInput.setDescription(description);
        createCardInput.setEstimate(estimate);
        createCardInput.setNote(note);
        createCardInput.setDeadline(deadline);
        createCardInput.setBoardId(boardId.id());
        CqrsCommandPresenter addCardOutput = CqrsCommandPresenter.newInstance();

        var output = createCardUseCase.execute(createCardInput);

        Card card = cardRepository.findById(CardId.valueOf(output.getId())).get();
        return card;
    }

    public void deleteCardUseCase(BoardId boardId, String workflowId, String laneId, String cardId, String userId) {
        DeleteCardUseCase deleteCardUseCase = newDeleteCardUseCase();

        DeleteCardInput input = new DeleteCardInput();
        input.setWorkflowId(workflowId);
        input.setLaneId(laneId);
        input.setCardId(cardId);
        input.setUserId(userId);
        input.setBoardId(boardId.id());

        deleteCardUseCase.execute(input);
    }

    public void moveCardUseCase(BoardId boardId, String cardId, String workflowId, String originalLaneId, String newLaneId, int order, String userId) {
        MoveCardUseCase moveCardUseCase = newMoveCardUseCase();

        MoveCardInput input = new MoveCardInput();
        input.setCardId(cardId);
        input.setWorkflowId(workflowId);
        input.setOldLaneId(originalLaneId);
        input.setNewLaneId(newLaneId);
        input.setOrder(order);
        input.setUserId(userId);
        input.setBoardId(boardId.id());
        input.setVersion(cardRepository.findById(CardId.valueOf(cardId)).get().getVersion());

        moveCardUseCase.execute(input);
    }


    public void changeCardDescriptionUseCase(BoardId boardId, String cardId, String newDescription, String userId){
        ChangeCardDescriptionUseCase changeCardDescriptionUseCase = newChangeCardDescriptionUseCase();

        ChangeCardDescriptionInput input = new ChangeCardDescriptionInput();
        input.setCardId(cardId);
        input.setDescription(newDescription);
        input.setBoardId(boardId.id());
        input.setUserId(userId);
        input.setVersion(cardRepository.findById(CardId.valueOf(cardId)).get().getVersion());

        changeCardDescriptionUseCase.execute(input);
    }

    protected static void createEsdbProjection(String projectionName, String scriptFileName, EventStoreDBProjectionManagementClient client)
            throws java.lang.InterruptedException {

        String projectionScript = readProjectionScript(scriptFileName);

        try {
            client.create(projectionName, projectionScript).get();
            UpdateProjectionOptions options = UpdateProjectionOptions.get().emitEnabled(true);
            client.update(projectionName, projectionScript, options).get();
        } catch (ExecutionException ex) {
            if (ex.getMessage().contains("Conflict")) {
                System.out.println(projectionName + " already exists");
            }
        }
    }

    protected static String readProjectionScript(String fileName){
        try (InputStream inputStream = SharedSpringBootJpaTest.class.getResourceAsStream("/esdb/" + fileName)) {
            byte[] bdata = inputStream.readAllBytes();
            return new String(bdata, StandardCharsets.UTF_8)
                    .replace("\r", "")
                    .replace("\n", "");
        } catch(IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected Card createCardUseCase() {
        return createCardUseCase(boardId.id(), workflowId, rootStageId);
    }

    protected Card createCardUseCase(String boardId, String workflowId, String laneId) {
        return createCardUseCase(
                BoardId.valueOf(boardId),
                workflowId,
                laneId,
                "firstCard",
                "s",
                "note",
                DateProvider.now(),
                userId);
    }

    protected CardId createCardUseCase(String boardId, String workflowId, String laneId, Date deadline) {
        return createCardUseCase(
                BoardId.valueOf(boardId),
                workflowId,
                laneId,
                "firstCard",
                "s",
                "note",
                deadline,
                userId).getCardId();
    }


    protected String createSwimlaneUseCase(String workflowId, String rootStageId) {
        return createSwimlaneUseCase(workflowId,
                rootStageId,
                "Expedite",
                WipLimit.UNLIMIT.value(),
                LaneType.Standard.name(),
                userId,
                boardId);
    }

    protected String createStageUseCase(String workflowId, String parentId) {
        return createStageUseCase(workflowId,
                parentId,
                "Plan",
                WipLimit.UNLIMIT.value(),
                LaneType.Standard.name(),
                userId,
                boardId);
    }

    protected String createRootStageUseCase(BoardId boardId, String workflowId) {
        return createStageUseCase(workflowId,
                NullLane.ID.id(),
                "Main",
                WipLimit.UNLIMIT.value(),
                LaneType.Standard.name(),
                userId,
                boardId
        );
    }

    public BoardRepository getBoardRepository(){
        return boardRepository;
    }


}
