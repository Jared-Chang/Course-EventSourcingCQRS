package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.AbstractRepository;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

public interface BoardRepository extends AbstractRepository<Board, BoardId> {
}
