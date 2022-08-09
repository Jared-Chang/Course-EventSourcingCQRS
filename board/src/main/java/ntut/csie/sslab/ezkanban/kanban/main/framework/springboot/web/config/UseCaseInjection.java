package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardUseCase;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.CreateBoardService;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentStateRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.boardcontent.NotifyBoardContent;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.NotifyBoardContentService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.rename.RenameLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.RenameLaneService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.stage.CreateStageUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateStageService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane.CreateSwimLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateSwimLaneService;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service.CreateWorkflowUseCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("KanbanUseCaseInjection")
@AutoConfigureAfter({RepositoryInjection.class})
public class UseCaseInjection {

    public static final String NOTIFY_BOARD_CONTENT_SERVICE_ID = "NotifyBoardContentService";

    private BoardRepository boardRepository;
    private WorkflowRepository workflowRepository;

    private BoardContentStateRepository boardContentStateRepository;
    private DomainEventBus eventBus;

    @Autowired
    public UseCaseInjection(
            BoardRepository boardRepository,
            WorkflowRepository workflowRepository,
            BoardContentStateRepository boardContentStateRepository,
            DomainEventBus eventBus
    ){

        this.boardRepository = boardRepository;
        this.workflowRepository = workflowRepository;
        this.boardContentStateRepository = boardContentStateRepository;
        this.eventBus = eventBus;
    }

    @Bean
    public CreateBoardUseCase createBoardUseCaseInBoard() {
        return new CreateBoardService(boardRepository, eventBus);
    }

    @Bean
    public CreateWorkflowUseCase createWorkflowUseCase() {
        return new CreateWorkflowUseCaseService(workflowRepository);
    }

    @Bean
    public CreateStageUseCase createStageUseCase() {
        return new CreateStageService(workflowRepository, eventBus);
    }

    @Bean
    public CreateSwimLaneUseCase createSwimLaneUseCase() {
        return new CreateSwimLaneService(workflowRepository, eventBus);
    }

    @Bean
    public RenameLaneUseCase renameLaneUseCase() {
        return new RenameLaneService(workflowRepository, eventBus);
    }

    @Bean
    public NotifyBoardContent notifyBoardContentProjector() {
        return new NotifyBoardContentService(NOTIFY_BOARD_CONTENT_SERVICE_ID, boardContentStateRepository);
    }

}
