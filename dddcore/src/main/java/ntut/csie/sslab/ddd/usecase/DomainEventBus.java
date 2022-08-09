package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;

public interface DomainEventBus {
    void post(DomainEvent domainEvent);
    void postAll(AggregateRoot aggregateRoot);
    void register(Object object);
    void unregister(Object object);
}
