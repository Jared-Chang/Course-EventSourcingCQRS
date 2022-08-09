package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.GenericOutboxRepository;
import ntut.csie.sslab.ddd.usecase.OutboxStore;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.List;
import java.util.Optional;

public class BoardOutboxRepository implements BoardRepository{
    private final GenericOutboxRepository<Board, BoardData, BoardId> outboxRepository;

    public BoardOutboxRepository(OutboxStore<BoardData, String> store) {
        outboxRepository = new GenericOutboxRepository<>(store, BoardMapper.newMapper());
    }

    @Override
    public Optional<Board> findById(BoardId boardId) {
        return outboxRepository.findById(boardId);
    }

    @Override
    public void save(Board board) {
        outboxRepository.save(board);
    }

    @Override
    public void delete(Board board) {
        outboxRepository.delete(board);
    }
}
