package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;


import ntut.csie.sslab.ddd.usecase.AbstractRepository;

import java.util.Date;
import java.util.Optional;

public interface BoardContentReadModelRepository extends AbstractRepository<BoardContentViewModel, String> {
    Optional<BoardContentViewModel> getBoardContent(String boardId, Date endDate);
}
