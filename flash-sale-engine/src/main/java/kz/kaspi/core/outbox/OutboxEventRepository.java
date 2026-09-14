package kz.kaspi.core.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    // Найти все события с определенным статусом, отсортированные по времени
    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(String status);
}
