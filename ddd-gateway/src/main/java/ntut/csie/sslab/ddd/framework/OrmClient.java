package ntut.csie.sslab.ddd.framework;

import ntut.csie.sslab.ddd.usecase.OutboxData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrmClient<T extends OutboxData, ID> extends CrudRepository<T, ID> {
    @Transactional
    default long saveAndUpdateVersion(T outboxData) {
        final long expectedVersion = getExpectedVersion(outboxData);
        save(outboxData);
        if (outboxData.getDomainEventDatas().size() > 1) {
            updateVersion(outboxData.getId(), expectedVersion);
        }
        return expectedVersion;
    }

    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} as c SET c.version = :version WHERE c.id = :id")
    void updateVersion(@Param("id") String id, @Param("version") long version);

    default long getExpectedVersion(T outboxData) {
        return outboxData.getVersion() + outboxData.getDomainEventDatas().size();
    }
}
