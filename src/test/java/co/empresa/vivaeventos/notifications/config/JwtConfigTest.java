package co.empresa.vivaeventos.notifications.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "this-is-a-secret-key-that-is-at-least-256-bits-long-for-hs256".getBytes()
    );

    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig(SECRET);
    }

    @Test
    @DisplayName("should validate a valid token")
    void shouldValidateValidToken() {
        var signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        String token = Jwts.builder()
                .subject("test-user")
                .claim("role", "SYSTEM")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        Claims claims = jwtConfig.validateToken(token);

        assertNotNull(claims);
        assertEquals("test-user", claims.getSubject());
        assertEquals("SYSTEM", claims.get("role"));
    }

    @Test
    @DisplayName("should throw on expired token")
    void shouldThrowOnExpiredToken() {
        var signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        String token = Jwts.builder()
                .subject("test-user")
                .issuedAt(Date.from(Instant.now().minusSeconds(3600)))
                .expiration(Date.from(Instant.now().minusSeconds(1800)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtConfig.validateToken(token));
    }

    @Test
    @DisplayName("should throw on malformed token")
    void shouldThrowOnMalformedToken() {
        assertThrows(Exception.class, () -> jwtConfig.validateToken("invalid-token"));
    }

    @Test
    @DisplayName("should throw on token signed with different key")
    void shouldThrowOnWrongKey() {
        String wrongSecret = Base64.getEncoder().encodeToString(
                "a-different-secret-key-that-is-also-256-bits-long-for-testing".getBytes()
        );
        var wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(wrongSecret));
        String token = Jwts.builder()
                .subject("test-user")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(wrongKey, Jwts.SIG.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtConfig.validateToken(token));
    }
}
