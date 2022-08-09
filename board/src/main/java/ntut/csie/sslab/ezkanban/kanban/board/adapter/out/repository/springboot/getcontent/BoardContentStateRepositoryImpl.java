package ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent;

import ntut.csie.sslab.ddd.adapter.repository.IdempotentRepositoryPeer;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentState;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentStateRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentViewData;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class BoardContentStateRepositoryImpl implements BoardContentStateRepository {
    private BoardContentReadModelRepositoryPeer boardContentRepositoryPeer;
    private IdempotentRepositoryPeer idempotentRepositoryPeer;

    public BoardContentStateRepositoryImpl(BoardContentReadModelRepositoryPeer boardContentRepositoryPeer,
                                           IdempotentRepositoryPeer idempotentRepositoryPeer) {
        requireNotNull("BoardContentReadModelRepositoryPeer", boardContentRepositoryPeer);

        this.boardContentRepositoryPeer = boardContentRepositoryPeer;
        this.idempotentRepositoryPeer = idempotentRepositoryPeer;
    }

    @Override
    public Optional<BoardContentState> findById(String boardId) {
        Optional<BoardContentViewData> viewData = boardContentRepositoryPeer.findById(boardId);
        if(viewData.isPresent()) {
            return Optional.of(Json.readValue(viewData.get().getViewModel(), BoardContentState.class));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void save(BoardContentState state) {
        BoardContentViewData entity = new BoardContentViewData(state.boardState().boardId().id(), Json.asString(state));
        boardContentRepositoryPeer.save(entity);
        idempotentRepositoryPeer.save(state.idempotentData());
    }

    @Transactional
    @Override
    public void delete(BoardContentState state) {
        boardContentRepositoryPeer.deleteById(state.boardState().boardId().id());
        idempotentRepositoryPeer.delete(state.idempotentData());
    }

    @Override
    public boolean isEventHandled(String handlerId, String eventId) {
        if (idempotentRepositoryPeer.isEventHandled(handlerId, eventId).isPresent())
            return true;
        else
            return false;
    }
}
