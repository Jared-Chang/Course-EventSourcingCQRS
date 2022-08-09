package ntut.csie.sslab.ezkanban.kanban.board.usecase.service;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.board.NotifyBoard;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;

import java.util.Optional;

public class NotifyBoardService implements NotifyBoard {
    private BoardRepository boardRepository;
    private DomainEventBus domainEventBus;

    public NotifyBoardService(BoardRepository boardRepository,
                              DomainEventBus domainEventBus) {
        this.boardRepository = boardRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public void whenWorkflowCreated(WorkflowEvents.WorkflowCreated workflowCreated) {
        Optional<Board> board = boardRepository.findById(workflowCreated.boardId());
        if (!board.isPresent())
            throw new RuntimeException("Board not found, board id = " + workflowCreated.boardId());

        board.get().commitWorkflow(workflowCreated.workflowId());
        boardRepository.save(board.get());
        domainEventBus.postAll(board.get());
    }

    @Override
    public void whenWorkflowDeleted(WorkflowEvents.WorkflowDeleted workflowDeleted) {
        Optional<Board> board = boardRepository.findById(workflowDeleted.boardId());
        if (!board.isPresent())
            throw new RuntimeException("Board not found, board id = " + workflowDeleted.boardId());

        board.get().uncommitworkflow(workflowDeleted.workflowId());
        boardRepository.save(board.get());
        domainEventBus.postAll(board.get());
    }
}
