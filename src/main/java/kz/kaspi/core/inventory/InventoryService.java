package kz.kaspi.core.inventory;

import java.util.Collections;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final StringRedisTemplate redisTemplate;

    private final RedisScript<Long> decrementStockScript;

    private static final String STOCK_KEY_PREFIX = "product:stock:";

    /**
     * Attempts to reserve product stock.
     * 
     * @param productId Product ID
     * @param amount    Number of items (usually 1)
     * @return true if successfully reserved, false if out of stock
     */
    public boolean reserveStock(Long productId, int amount) {
        String redisKey = STOCK_KEY_PREFIX + productId;

        try {
            Long result = redisTemplate.execute(
                    decrementStockScript,
                    Collections.singletonList(redisKey),
                    String.valueOf(amount));

            if (result != null && result == -1L) {
                log.warn("Overbooking detected, Product {} sailed.", productId);
                return false;
            }

            log.info("Successfully booked {} pieces of goods {}. New balance: {}", amount, productId, result);
            return true;
        } catch (Exception e) {
            log.error("Critical error when accessing Redis for a product {}", productId, e);
            return false;
        }
    }

    public void initStock(Long productId, int initialStock) {
        String redisKey = STOCK_KEY_PREFIX + productId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(initialStock));
        log.info("The remaining balance for the product {} has been uploaded to Redis: {} pcs.", productId,
                initialStock);
    }

    public void rollbackStock(Long productId, int amount) {
        String redisKey = "product:stock:" + productId;
        redisTemplate.opsForValue().increment(redisKey, amount);
        log.info("Компенсация: {} шт. товара {} возвращены в Redis.", amount, productId);
    }
}
