package co.empresa.vivaeventos.notifications.infrastructure.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailProviderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private SmtpEmailProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SmtpEmailProvider(mailSender);
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
    @DisplayName("send should delegate to mailSender")
    void sendShouldDelegateToMailSender() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> provider.send("to@test.com", "Subject", "<p>Body</p>"));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("send should throw on mail error")
    void sendShouldThrowOnError() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(MimeMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> provider.send("to@test.com", "Subject", "<p>Body</p>"));
        assertTrue(ex.getMessage().contains("SMTP"));
    }

    @Test
    @DisplayName("getProviderName should return smtp")
    void getProviderNameShouldReturnSmtp() {
        assertEquals("smtp", provider.getProviderName());
    }
}
