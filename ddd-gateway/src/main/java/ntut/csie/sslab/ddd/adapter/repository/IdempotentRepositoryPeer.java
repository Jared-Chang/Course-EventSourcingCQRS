package ntut.csie.sslab.ddd.adapter.repository;

import ntut.csie.sslab.ddd.usecase.IdempotentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IdempotentRepositoryPeer extends JpaRepository<IdempotentData, Long> {

    @Query(value = "SELECT 1 FROM IdempotentData as c WHERE c.handlerId = :handlerId and c.eventId = :eventId")
    Optional<Integer> isEventHandled(@Param("handlerId") String handlerId, @Param("eventId") String eventId);
}
