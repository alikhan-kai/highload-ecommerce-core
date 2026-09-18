package kz.kaspi.core.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    /**
     * Точка входа для Flash-Sale (оформление заказа)
     * Пример запроса: POST http://localhost:8080/api/orders?userId=1&productId=500
     * Обязательный заголовок: Idempotency-Key
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestParam("productId") Long productId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        try {
            Long userId = (Long) org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            Order order = orderService.placeOrder(userId, productId, idempotencyKey);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}