package plataformaSaude.configuration;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.SimpleMailMessage;

import java.util.Properties;

/**
 * For development (profile 'local'): provide a no-op JavaMailSender so the app
 * doesn't try to connect to a real SMTP server (localhost:25) when sending emails.
 */
@Configuration
@Profile("local")
public class DevMailConfig {

    private static final Logger logger = LoggerFactory.getLogger(DevMailConfig.class);

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return new NoOpJavaMailSender();
    }

    private static class NoOpJavaMailSender implements JavaMailSender {
        private final Logger log = LoggerFactory.getLogger(NoOpJavaMailSender.class);
        private final Session session = Session.getDefaultInstance(new Properties());

        @Override
        public MimeMessage createMimeMessage() {
            return new MimeMessage(session);
        }

        @Override
        public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
            try {
                return new MimeMessage(session, contentStream);
            } catch (jakarta.mail.MessagingException e) {
                // If creation from stream fails, return an empty MimeMessage
                log.warn("[DEV-MAIL] Failed to create MimeMessage from stream, returning empty message", e);
                return new MimeMessage(session);
            }
        }

        @Override
        public void send(MimeMessage mimeMessage) {
            log.info("[DEV-MAIL] send(MimeMessage) called — message not sent in local profile");
        }

        @Override
        public void send(MimeMessage... mimeMessages) {
            log.info("[DEV-MAIL] send(MimeMessage...) called — {} messages ignored", mimeMessages.length);
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) {
            log.info("[DEV-MAIL] send(MimeMessagePreparator) called — message ignored");
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) {
            log.info("[DEV-MAIL] send(MimeMessagePreparator...) called — {} preparators ignored", mimeMessagePreparators.length);
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) {
            log.info("[DEV-MAIL] send(SimpleMailMessage) to={} subject={}:\n{}", (Object) simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getText());
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) {
            log.info("[DEV-MAIL] send(SimpleMailMessage...) called — {} messages ignored", simpleMessages.length);
            for (SimpleMailMessage m : simpleMessages) {
                log.info("[DEV-MAIL] to={} subject={} body={} ", (Object) m.getTo(), m.getSubject(), m.getText());
            }
        }
    }
}
