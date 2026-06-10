package co.empresa.vivaeventos.notifications.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class EventsClientTest {

    private static final String SECRET = "dGhpcy1pcy1hLXNlY3JldC1rZXktdGhhdC1pcy1hdC1sZWFzdC0yNTYtYml0cy1sb25nLWZvci1oczI1Ng==";

    private RestTemplate restTemplate;
    private EventsClient eventsClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        objectMapper = new ObjectMapper();
        eventsClient = new EventsClient("http://localhost:8081", SECRET);
        var field = EventsClient.class.getDeclaredFields();
        for (var f : field) {
            f.setAccessible(true);
            if (f.getName().equals("restTemplate")) {
                try {
                    f.set(eventsClient, restTemplate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Test
    @DisplayName("getUpcomingEvents should return events list")
    void getUpcomingEventsShouldReturnEvents() throws Exception {
        List<Map<String, Object>> expected = List.of(
                Map.of("id", "550e8400-e29b-41d4-a716-446655440000", "name", "Event 1"),
                Map.of("id", "550e8400-e29b-41d4-a716-446655440001", "name", "Event 2")
        );

        mockServer.expect(requestTo("http://localhost:8081/api/v1/events/upcoming"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", containsString("Bearer")))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expected), MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = eventsClient.getUpcomingEvents();

        assertEquals(2, result.size());
        assertEquals("Event 1", result.getFirst().get("name"));
    }

    @Test
    @DisplayName("getUpcomingEvents should return empty list on error")
    void getUpcomingEventsShouldReturnEmptyOnError() {
        mockServer.expect(requestTo("http://localhost:8081/api/v1/events/upcoming"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        List<Map<String, Object>> result = eventsClient.getUpcomingEvents();

        assertTrue(result.isEmpty());
    }
}
