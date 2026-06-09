package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    EmailService emailService;

    @Test
    void enviarEmail_DeveMontarEMandarMsgCorreta() {

        // act
        emailService.enviarEmail("destino@test.com", "Assunto X", "Corpo Y");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage enviada = captor.getValue();

        // assert
        assertThat(enviada.getTo()).containsExactly("destino@test.com");
        assertThat(enviada.getSubject()).isEqualTo("Assunto X");
        assertThat(enviada.getText()).isEqualTo("Corpo Y");
    }
}

