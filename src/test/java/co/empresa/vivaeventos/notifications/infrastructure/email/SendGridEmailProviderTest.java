package co.empresa.vivaeventos.notifications.infrastructure.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class SendGridEmailProviderTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private SendGridEmailProvider provider;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        provider = new SendGridEmailProvider(
                "test-api-key",
                "from@test.com",
                "VivaEventos",
                restTemplate,
                objectMapper
        );
    }

    @Test
    @DisplayName("supports should return true for EMAIL")
    void supportsShouldReturnTrueForEmail() {
        assertTrue(provider.supports("EMAIL"));
        assertTrue(provider.supports("email"));
    }

    @Test
    @DisplayName("supports should return false for other channels")
    void supportsShouldReturnFalseForOther() {
        assertFalse(provider.supports("SMS"));
        assertFalse(provider.supports("WHATSAPP"));
    }

    @Test
    @DisplayName("send should call SendGrid API and succeed")
    void sendShouldCallSendGridAndSucceed() {
        mockServer.expect(requestTo("https://api.sendgrid.com/v3/mail/send"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-api-key"))
                .andRespond(withSuccess());

        assertDoesNotThrow(() -> provider.send("to@test.com", "Subject", "<p>Body</p>"));
        mockServer.verify();
    }

    @Test
    @DisplayName("send should throw on API error")
    void sendShouldThrowOnApiError() {
        mockServer.expect(requestTo("https://api.sendgrid.com/v3/mail/send"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThrows(RuntimeException.class, () -> provider.send("to@test.com", "Subject", "<p>Body</p>"));
    }

    @Test
    @DisplayName("getProviderName should return sendgrid")
    void getProviderNameShouldReturnSendgrid() {
        assertEquals("sendgrid", provider.getProviderName());
    }
}
