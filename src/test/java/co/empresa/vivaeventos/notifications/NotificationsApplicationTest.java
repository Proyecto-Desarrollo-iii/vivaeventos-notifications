package co.empresa.vivaeventos.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=dGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLXRlc3RpbmctcHVycG9zZXMtb25seQo=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.test-connection=false"
})
class NotificationsApplicationTest {

    @Test
    @DisplayName("application context loads")
    void contextLoads() {
    }
}
