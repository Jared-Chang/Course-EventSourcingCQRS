package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;

import java.util.Optional;

public interface BoardContentRepository {
    Optional<BoardContentViewModel> findById(BoardId boardId);
    void save(BoardContentViewModel model);

    void createBoard(BoardEvents.BoardCreated event);

    void addBoardMember(BoardEvents.BoardMemberAdded event, UserDto userDto);

    void renameBoard(BoardEvents.BoardRenamed event);

    void removeBoardMember(BoardEvents.BoardMemberRemoved event);

    void createWorkflow(WorkflowEvents.WorkflowCreated event);

    void renameWorkflow(WorkflowEvents.WorkflowRenamed event);

    void deleteWorkflow(WorkflowEvents.WorkflowDeleted event);

    void createStage(WorkflowEvents.StageCreated event);
}
