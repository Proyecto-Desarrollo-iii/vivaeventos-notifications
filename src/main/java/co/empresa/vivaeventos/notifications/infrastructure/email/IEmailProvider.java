package co.empresa.vivaeventos.notifications.infrastructure.email;

public interface IEmailProvider {

    boolean supports(String channel);

    void send(String recipient, String subject, String body);

    String getProviderName();
}
