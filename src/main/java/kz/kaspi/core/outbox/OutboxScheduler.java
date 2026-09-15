package kz.kaspi.core.outbox;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutboxEvent() {
        List<OutboxEvent> events = outboxRepository.findAllByStatusOrderByCreatedAtAsc("NEW");

        if (events.isEmpty()) {
            return;
        }
        log.info("Found {} new outbox events in kafka", events.size());

        for (OutboxEvent event : events) {
            try {
                String topic = event.getAggregateType().equals("Product") ? "product-events" : "order-events";
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());
                event.setStatus("PROCESSED");
            } catch (Exception e) {
                log.error("Error when sending an event to Kafka: {}", event.getId(), e);
            }
        }

        outboxRepository.saveAll(events);
    }
}
