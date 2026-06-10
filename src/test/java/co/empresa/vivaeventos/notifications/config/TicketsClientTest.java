package co.empresa.vivaeventos.notifications.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class TicketsClientTest extends BaseHttpClientTest {

    private TicketsClient ticketsClient;

    @BeforeEach
    void setUpClient() {
        ticketsClient = new TicketsClient(new RestTemplateBuilder(), "http://localhost:8085", SECRET);
        injectRestTemplate(ticketsClient, TicketsClient.class);
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
