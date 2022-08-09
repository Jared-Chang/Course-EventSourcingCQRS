package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.entity.AggregateRoot;

public interface OutboxMapper<T extends AggregateRoot, E extends OutboxData> {
    T toDomain(E data);
    E toData(T aggregateRoot);
}
