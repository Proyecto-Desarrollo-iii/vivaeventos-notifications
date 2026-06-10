package co.empresa.vivaeventos.notifications.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class TicketsClientTest {

    private static final String SECRET = "dGhpcy1pcy1hLXNlY3JldC1rZXktdGhhdC1pcy1hdC1sZWFzdC0yNTYtYml0cy1sb25nLWZvci1oczI1Ng==";

    private RestTemplate restTemplate;
    private TicketsClient ticketsClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        objectMapper = new ObjectMapper();
        ticketsClient = new TicketsClient("http://localhost:8085", SECRET);
        var field = TicketsClient.class.getDeclaredFields();
        for (var f : field) {
            f.setAccessible(true);
            if (f.getName().equals("restTemplate")) {
                try {
                    f.set(ticketsClient, restTemplate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Test
    @DisplayName("getIssuedTicketsByEvent should return tickets list")
    void getIssuedTicketsByEventShouldReturnTickets() throws Exception {
        UUID eventId = UUID.randomUUID();
        Map<String, Object> responseBody = Map.of(
                "boletas", List.of(
                        Map.of("userId", UUID.randomUUID().toString(), "holderEmail", "test@test.com", "holderName", "Test User"),
                        Map.of("userId", UUID.randomUUID().toString(), "holderEmail", "test2@test.com", "holderName", "Test User 2")
                )
        );

        mockServer.expect(requestTo("http://localhost:8085/api/v1/issued-tickets/event/" + eventId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", containsString("Bearer")))
                .andRespond(withSuccess(objectMapper.writeValueAsString(responseBody), MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = ticketsClient.getIssuedTicketsByEvent(eventId);

        assertEquals(2, result.size());
        assertEquals("test@test.com", result.getFirst().get("holderEmail"));
    }

    @Test
    @DisplayName("getIssuedTicketsByEvent should return empty when no boletas key")
    void getIssuedTicketsByEventShouldReturnEmptyWhenNoBoletas() throws Exception {
        UUID eventId = UUID.randomUUID();
        Map<String, Object> responseBody = Map.of("other", "data");

        mockServer.expect(requestTo("http://localhost:8085/api/v1/issued-tickets/event/" + eventId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(responseBody), MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = ticketsClient.getIssuedTicketsByEvent(eventId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getIssuedTicketsByEvent should return empty on error")
    void getIssuedTicketsByEventShouldReturnEmptyOnError() {
        UUID eventId = UUID.randomUUID();
        mockServer.expect(requestTo("http://localhost:8085/api/v1/issued-tickets/event/" + eventId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        List<Map<String, Object>> result = ticketsClient.getIssuedTicketsByEvent(eventId);

        assertTrue(result.isEmpty());
    }
}
