package kz.kaspi.core.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kaspi.core.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j 
public class PaymentSagaListener {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    @Transactional
    public void processPayment(String payload){
        try {
            Order orderEvent = objectMapper.readValue(payload, Order.class);
            log.info("Биллинг получил заказ {} на оплату...", orderEvent.getId());
            // Достаем оригинальный заказ из базы
            Order order = orderRepository.findById(orderEvent.getId()).orElseThrow();
            // Симулируем обращение к платежному шлюзу.
            // Пусть каждый 5-й заказ (20% шанс) падает с ошибкой "Недостаточно средств"
            boolean paymentSuccess = Math.random() > 0.2;
            if (paymentSuccess) {
                order.setStatus("COMPLETED");
                log.info("Оплата заказа {} прошла успешно!", order.getId());
        } else {
            order.setStatus("FAILED");
            log.warn("Payment for the order {} failed (Insufficient funds)!", order.getId());
            inventoryService.rollbackStock(order.getProductId(), 1);
        }
        orderRepository.save(order);
    } catch(Exception e){
        log.error("Error processing payment for order", e);
    }
}
}