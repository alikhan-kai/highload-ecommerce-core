package kz.kaspi.core.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_shouldReturnValidJwtToken() {
        Long userId = 12345L;
        String token = jwtService.generateToken(userId);
        
        assertNotNull(token);
        assertEquals(userId, jwtService.extractUserId(token));
    }
}
