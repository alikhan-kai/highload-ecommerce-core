package kz.kaspi.core.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kaspi.core.inventory.InventoryService;
import kz.kaspi.core.outbox.OutboxEvent;
import kz.kaspi.core.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Order placeOrder(Long userId, Long productId, String idempotencyKey) {

        String redisKey = "idempotency:" + idempotencyKey;
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(10));

        if (Boolean.FALSE.equals(isNewRequest)) {
            log.warn("Дублирующийся запрос отклонен! Ключ: {}", idempotencyKey);
            throw new RuntimeException("Запрос уже обрабатывается (Двойной клик)");
        }

        log.info("Клиент {} пытается купить товар {} (Ключ: {})", userId, productId, idempotencyKey);

        boolean reserved = inventoryService.reserveStock(productId, 1);

        if (!reserved) {
            log.warn("Reject to client {}. Product {} is sold out", userId, productId);
            throw new RuntimeException("Product is sold out!");
        }

        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        try {
            String payload = objectMapper.writeValueAsString(order);
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Order")
                    .aggregateId(String.valueOf(order.getId()))
                    .payload(payload)
                    .status("NEW")
                    .build();
            outboxEventRepository.save(event);
        } catch (Exception e) {
            log.error("Error when serializing an order in JSON", e);
            throw new RuntimeException("System error when creating an order");
        }

        log.info("Оформили заказ {} и положили в Outbox.", order.getId());
        return order;
    }
}
