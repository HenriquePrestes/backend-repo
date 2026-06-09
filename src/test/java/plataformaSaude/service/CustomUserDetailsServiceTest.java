package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @InjectMocks
    CustomUserDetailsService service;

    @Test
    void loadUserByUsername_DeveRetornarMedicoQuandoExiste() {

        plataformaSaude.model.Medico medico = mock(plataformaSaude.model.Medico.class);
        when(medicoRepository.findByEmail("abc@teste.com")).thenReturn(Optional.of(medico));
        UserDetails result = service.loadUserByUsername("abc@teste.com");
        assertThat(result).isEqualTo(medico);
    }

    @Test
    void loadUserByUsername_DeveRetornarPacienteQuandoMedicoNaoExiste() {

        plataformaSaude.model.Paciente paciente = mock(plataformaSaude.model.Paciente.class);
        when(medicoRepository.findByEmail("abc@teste.com")).thenReturn(Optional.empty());
        when(pacienteRepository.findByEmail("abc@teste.com")).thenReturn(Optional.of(paciente));
        UserDetails result = service.loadUserByUsername("abc@teste.com");
        assertThat(result).isEqualTo(paciente);
    }

    @Test
    void loadUserByUsername_DeveLancarExceptionQuandoNaoExisteEmNenhum() {

        when(medicoRepository.findByEmail("abc@teste.com")).thenReturn(Optional.empty());
        when(pacienteRepository.findByEmail("abc@teste.com")).thenReturn(Optional.empty());
        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("abc@teste.com")
        );
    }
}
