package ntut.csie.sslab.ezkanban.kanban.board.usecase.getboardcontent;


import jakarta.ws.rs.NotSupportedException;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentViewData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class InMemoryBoardContentReadModelRepositoryPeer implements BoardContentReadModelRepositoryPeer {

    private final Map<String, BoardContentViewData> store;

    public InMemoryBoardContentReadModelRepositoryPeer(){
        store = new HashMap<>();
    }


    @Override
    public <S extends BoardContentViewData> S save(S entity) {
        store.put(entity.getBoardId(), entity);
        return entity;
    }

    @Override
    public <S extends BoardContentViewData> Iterable<S> saveAll(Iterable<S> entities) {
        throw new NotSupportedException();
    }

    @Override
    public Optional<BoardContentViewData> findById(String boardId) {
        if (store.containsKey(boardId)){
            return Optional.of(store.get(boardId));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        throw new NotSupportedException();
    }

    @Override
    public Iterable<BoardContentViewData> findAll() {
        throw new NotSupportedException();
    }

    @Override
    public Iterable<BoardContentViewData> findAllById(Iterable<String> strings) {
        throw new NotSupportedException();
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(String boardId) {
        store.remove(boardId);
    }

    @Override
    public void delete(BoardContentViewData entity) {
        store.remove(entity.getBoardId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAll(Iterable<? extends BoardContentViewData> entities) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAll() {
        store.clear();
    }
}