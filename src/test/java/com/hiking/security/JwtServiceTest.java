package com.hiking.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // 32-byte key (256 bits) → valid for HS256
    private static final String SECRET =
            Base64.getEncoder().encodeToString("01234567890123456789012345678901".getBytes());

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3_600_000L); // 1 hour
        userDetails = User.withUsername("user@example.com")
                .password("pw")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generateToken_thenExtractUsername_roundTrips() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("user@example.com", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_returnsTrue_forMatchingUserAndUnexpiredToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_returnsFalse_whenUsernameDiffers() {
        String token = jwtService.generateToken(userDetails);
        UserDetails other = User.withUsername("someone@else.com")
                .password("pw").authorities("ROLE_USER").build();

        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    void expiredToken_throwsWhenParsed() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1_000L); // already expired
        String token = jwtService.generateToken(userDetails);

        // jjwt rejects expired tokens at parse time
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(token));
    }

    @Test
    void generateToken_producesDistinctTokensForDifferentUsers() {
        UserDetails other = User.withUsername("other@example.com")
                .password("pw").authorities("ROLE_ADMIN").build();

        String t1 = jwtService.generateToken(userDetails);
        String t2 = jwtService.generateToken(other);

        assertNotEquals(t1, t2);
        assertEquals("other@example.com", jwtService.extractUsername(t2));
    }

    @Test
    void tokenSignedWithDifferentKey_isRejected() {
        String token = jwtService.generateToken(userDetails);

        JwtService otherService = new JwtService();
        ReflectionTestUtils.setField(otherService, "secretKey",
                Base64.getEncoder().encodeToString("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz".getBytes()));
        ReflectionTestUtils.setField(otherService, "expiration", 3_600_000L);

        assertThrows(Exception.class, () -> otherService.extractUsername(token));
    }
}
