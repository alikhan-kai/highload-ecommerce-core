package kz.kaspi.core.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.context.annotation.Bean;


@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> decrementStockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/decrement_stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
