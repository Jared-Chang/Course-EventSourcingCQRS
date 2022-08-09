package ntut.csie.sslab.ddd.usecase;

import java.util.List;
import java.util.Optional;

public interface OutboxStore<T extends OutboxData, ID> {
    void save(T data);
    Optional<T> findById(ID id);
    void delete(T data);
    List<DomainEventData> getCategoryEvent(String categoryName);
    default String getStreamName(String category, String id) {
        return category + "-" + id;
    }
}
