package kz.kaspi.core.order;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kz.kaspi.core.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kz.kaspi.core.outbox.OutboxEvent;
import kz.kaspi.core.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @Transactional 
    public Order placeOrder(Long userId, Long productId){
        log.info("Client {} try to buy product {}", userId, productId);

        boolean reserved = inventoryService.reserveStock(productId, 1);

        if(!reserved){
            log.warn("Reject to client {}. Product {} is sold out", userId, productId);
            throw new RuntimeException("Product is sold out!");
        }

        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        try{
            String payload = objectMapper.writeValueAsString(order);
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Order")
                    .aggregateId(String.valueOf(order.getId()))
                    .payload(payload)
                    .status("NEW")
                    .build();
            outboxEventRepository.save(event);
        } catch(Exception e){
            log.error("Error when serializing an order in JSON", e);
            throw new RuntimeException("System error when creating an order");
        }
        log.info("The order {} has been successfully created and is awaiting payment. The event has been added to the Outbox.", order.getId());
        return order;
    }

}
