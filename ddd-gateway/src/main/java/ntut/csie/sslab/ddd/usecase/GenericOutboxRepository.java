package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class GenericOutboxRepository<T extends AggregateRoot, E extends OutboxData, ID>
        implements AbstractRepository<T, ID> {
    private OutboxStore<E, String> store;
    private final OutboxMapper<T, E> mapper;
    public GenericOutboxRepository(OutboxStore outboxStore, OutboxMapper mapper) {
        requireNotNull("OutboxStore", outboxStore);
        requireNotNull("OutboxMapper", mapper);
        this.store = outboxStore;
        this.mapper = mapper;
    }

    @Override
    public Optional<T> findById(ID id) {
        requireNotNull("id", id);
        Optional<E> data = store.findById(id.toString());
        if(data.isPresent()) {
            return Optional.of(mapper.toDomain(data.get()));
        }
        return Optional.empty();
    }

    @Override
    public void save(T board) {
        try {
            E data = mapper.toData(board);
            store.save(data);
            board.setVersion(data.getVersion());
            board.clearDomainEvents();
        }
        catch (ObjectOptimisticLockingFailureException e){
            throw new RepositorySaveException(
                    RepositorySaveException.OPTIMISTIC_LOCKING_FAILURE, e);
        }
    }

    @Override
    public void delete(T board) {
        store.delete(mapper.toData(board));
    }

}
