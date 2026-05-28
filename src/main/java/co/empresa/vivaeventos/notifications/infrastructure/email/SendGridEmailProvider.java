package co.empresa.vivaeventos.notifications.infrastructure.email;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "notifications.sendgrid.api-key")
@Slf4j
public class SendGridEmailProvider implements IEmailProvider {

    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";

    private final String apiKey;
    private final String fromEmail;
    private final String fromName;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SendGridEmailProvider(
            @Value("${notifications.sendgrid.api-key}") String apiKey,
            @Value("${notifications.sendgrid.from-email}") String fromEmail,
            @Value("${notifications.sendgrid.from-name:VivaEventos}") String fromName,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }

    @Override
    public void send(String recipient, String subject, String body) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();

            ObjectNode from = objectMapper.createObjectNode();
            from.put("email", fromEmail);
            from.put("name", fromName);
            payload.set("from", from);

            ObjectNode personalization = objectMapper.createObjectNode();
            ArrayNode toArray = objectMapper.createArrayNode();
            ObjectNode toEntry = objectMapper.createObjectNode();
            toEntry.put("email", recipient);
            toArray.add(toEntry);
            personalization.set("to", toArray);
            personalization.put("subject", subject);

            ArrayNode personalizations = objectMapper.createArrayNode();
            personalizations.add(personalization);
            payload.set("personalizations", personalizations);

            ArrayNode content = objectMapper.createArrayNode();
            ObjectNode contentEntry = objectMapper.createObjectNode();
            contentEntry.put("type", "text/html");
            contentEntry.put("value", body);
            content.add(contentEntry);
            payload.set("content", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(SENDGRID_URL, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent via SendGrid to {}: {}", recipient, subject);
            } else {
                throw new RuntimeException("SendGrid responded with " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via SendGrid to " + recipient + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "sendgrid";
    }
}
