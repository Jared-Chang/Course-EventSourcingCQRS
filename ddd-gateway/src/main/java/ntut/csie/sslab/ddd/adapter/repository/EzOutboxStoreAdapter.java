package ntut.csie.sslab.ddd.adapter.repository;

import ntut.csie.sslab.ddd.framework.EzOutboxStore;
import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;
import ntut.csie.sslab.ddd.usecase.OutboxStore;

import java.util.List;
import java.util.Optional;

public class EzOutboxStoreAdapter<T extends OutboxData, ID> implements OutboxStore<T, ID> {
    private EzOutboxStore<T, ID> outboxStore;
    public EzOutboxStoreAdapter(EzOutboxStore outboxStore) {
        this.outboxStore = outboxStore;
    }

    @Override
    public void save(T data) {
        long version = outboxStore.save(data);
        data.setVersion(version);
    }

    @Override
    public Optional<T> findById(ID id) {
        return outboxStore.findById(id);
    }

    @Override
    public List<DomainEventData> getCategoryEvent(String categoryName) {
        return outboxStore.getCategoryEvent(categoryName);
    }

    @Override
    public void delete(T data) {
        outboxStore.delete(data);
    }
}
