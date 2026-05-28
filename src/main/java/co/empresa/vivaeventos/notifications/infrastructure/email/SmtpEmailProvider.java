package co.empresa.vivaeventos.notifications.infrastructure.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(SendGridEmailProvider.class)
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailProvider implements IEmailProvider {

    private final JavaMailSender mailSender;

    @Override
    public boolean supports(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }

    @Override
    public void send(String recipient, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email sent via SMTP to {}: {}", recipient, subject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via SMTP to " + recipient + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "smtp";
    }
}
