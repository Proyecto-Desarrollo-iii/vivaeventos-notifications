package co.empresa.vivaeventos.notifications.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

abstract class BaseHttpClientTest {

    protected static final String SECRET = "dGhpcy1pcy1hLXNlY3JldC1rZXktdGhhdC1pcy1hdC1sZWFzdC0yNTYtYml0cy1sb25nLWZvci1oczI1Ng==";

    protected RestTemplate restTemplate;
    protected MockRestServiceServer mockServer;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        objectMapper = new ObjectMapper();
    }

    protected void injectRestTemplate(Object client, Class<?> clientClass) {
        for (var f : clientClass.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getName().equals("restTemplate")) {
                try {
                    f.set(client, restTemplate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
