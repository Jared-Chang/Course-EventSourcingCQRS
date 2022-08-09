package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ddd.usecase.GenericEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.List;
import java.util.Optional;

public class BoardEventSourcingRepository implements BoardRepository {
    private final GenericEventSourcingRepository<Board> eventSourcingRepository;

    public BoardEventSourcingRepository(EventStore eventStore) {
        eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, Board.class, Board.CATEGORY);
    }

    @Override
    public Optional<Board> findById(BoardId boardId) {
        return eventSourcingRepository.findById(boardId.id());
    }

    @Override
    public void save(Board board) {
        eventSourcingRepository.save(board);
    }

    @Override
    public void delete(Board board) {
        eventSourcingRepository.delete(board);
    }

    @Override
    public void close() {
        eventSourcingRepository.close();
    }
}

