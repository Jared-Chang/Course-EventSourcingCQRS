package ntut.csie.sslab.ezkanban.kanban.board.usecase.getboardcontent;

import jakarta.ws.rs.NotSupportedException;
import ntut.csie.sslab.ddd.adapter.repository.IdempotentRepositoryPeer;
import ntut.csie.sslab.ddd.usecase.IdempotentData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryIdempotentRepositoryPeer implements IdempotentRepositoryPeer {

    private final Map<String, String> store;

    public InMemoryIdempotentRepositoryPeer(){
        store = new HashMap<>();
    }

    @Override
    public Optional<Integer> isEventHandled(String handlerId, String eventId) {
        if (store.containsKey(handlerId) && store.containsValue(eventId))
            return Optional.of(1);
        return Optional.empty();
    }

    @Override
    public List<IdempotentData> findAll() {
        throw new NotSupportedException();
    }

    @Override
    public List<IdempotentData> findAll(Sort sort) {
        throw new NotSupportedException();
    }

    @Override
    public Page<IdempotentData> findAll(Pageable pageable) {
        throw new NotSupportedException();
    }

    @Override
    public List<IdempotentData> findAllById(Iterable<Long> longs) {
        throw new NotSupportedException();
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(Long aLong) {
        throw new NotSupportedException();
    }

    @Override
    public void delete(IdempotentData entity) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAll(Iterable<? extends IdempotentData> entities) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAll() {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> S save(S entity) {
        store.put(entity.getHandlerId(), entity.getHandlerId());
        return entity;
    }

    @Override
    public <S extends IdempotentData> List<S> saveAll(Iterable<S> entities) {
        throw new NotSupportedException();
    }

    @Override
    public Optional<IdempotentData> findById(Long aLong) {
        throw new NotSupportedException();
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new NotSupportedException();
    }

    @Override
    public void flush() {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> S saveAndFlush(S entity) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAllInBatch(Iterable<IdempotentData> entities) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        throw new NotSupportedException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new NotSupportedException();
    }

    @Override
    public IdempotentData getOne(Long aLong) {
        throw new NotSupportedException();
    }

    @Override
    public IdempotentData getById(Long aLong) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> Optional<S> findOne(Example<S> example) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> List<S> findAll(Example<S> example) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> List<S> findAll(Example<S> example, Sort sort) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> long count(Example<S> example) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends IdempotentData> boolean exists(Example<S> example) {
        throw new NotSupportedException();
    }
}
