package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.NotifyBoardService;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.board.NotifyBoard;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google.NotifyBoardAdapter;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotifyInjection {
    private BoardRepository boardRepository;
    private WorkflowRepository workflowRepository;
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private DomainEventBus eventBus;

    @Autowired
    public NotifyInjection(
            BoardRepository boardRepository,
            WorkflowRepository workflowRepository,
            CardRepository cardRepository,
            UserRepository userRepository,
            DomainEventBus eventBus){

        this.boardRepository = boardRepository;
        this.workflowRepository = workflowRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Bean
    public NotifyBoard notifyBoard() {
        return new NotifyBoardService(boardRepository, eventBus);
    }

    @Bean
    public NotifyBoardAdapter notifyBoardAdapter() {
        return new NotifyBoardAdapter(notifyBoard());
    }

}
