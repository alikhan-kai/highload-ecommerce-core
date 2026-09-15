package kz.kaspi.core.search;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSyncListener {

    private final ProductSearchRepository productSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-events", groupId = "flash-sale-group")
    public void consumeProductEvent(String payload) {
        log.info("Event received from Kafka (product-events): {}", payload);

        try {
            ProductDocument document = objectMapper.readValue(payload, ProductDocument.class);
            productSearchRepository.save(document);
            log.info("The product {} has been successfully synchronized with Elasticsearch", document.getSku());
        } catch (Exception e) {
            log.error("Error when processing the product synchronization event: {}", payload, e);
        }
    }
}
