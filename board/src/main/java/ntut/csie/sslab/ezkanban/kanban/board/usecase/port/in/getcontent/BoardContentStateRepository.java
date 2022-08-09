package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ddd.usecase.AbstractRepository;

public interface BoardContentStateRepository extends AbstractRepository<BoardContentState, String> {

    boolean isEventHandled(String handlerId, String eventId);
//    boolean isExist(String boardId);
}
