package ntut.csie.sslab.ddd.usecase;

import com.eventstore.dbclient.WrongExpectedVersionException;
import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class GenericEventSourcingRepository<T extends AggregateRoot>
        implements AbstractRepository<T, String> {
    private final EventStore eventStore;
    private final Class clazz;
    private final String category;
    public GenericEventSourcingRepository(EventStore eventStore,
                                          Class clazz, String category) {
        this.eventStore = eventStore;
        this.clazz = clazz;
        this.category = category;
    }

    @Override
    public Optional<T> findById(String aggregateId) {
        requireNotNull("AggregateId", aggregateId);
        Optional<AggregateRootData> aggregateRootData =
                eventStore.load(AggregateRoot.getStreamName(category, aggregateId));
        if (aggregateRootData.isEmpty()) {
            return Optional.empty();
        }
        List<DomainEvent> domainEvents = DomainEventMapper.
                toDomain(aggregateRootData.get().getDomainEventDatas());
        try {
            T aggregate = (T) clazz.getConstructor(List.class).newInstance(domainEvents);
            aggregate.setVersion(aggregateRootData.get().getVersion());
            if (aggregate.isDeleted())
                return Optional.empty();
            else
                return Optional.of(aggregate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(T aggregate) {
        requireNotNull("Aggregate", aggregate);
        try {
            AggregateRootData aggregateRootData = AggregateMapper.toData(aggregate);
            eventStore.save(aggregateRootData);
            aggregate.setVersion(aggregateRootData.getVersion());
            aggregate.clearDomainEvents();
        }
        catch (ObjectOptimisticLockingFailureException | WrongExpectedVersionException e){
            throw new RepositorySaveException(RepositorySaveException.OPTIMISTIC_LOCKING_FAILURE, e);
        }
    }

    @Override
    public void delete(T aggregate) {
        requireNotNull("Aggregate", aggregate);

        save(aggregate);
    }

    @Override
    public void close() {
        eventStore.close();
    }

}
