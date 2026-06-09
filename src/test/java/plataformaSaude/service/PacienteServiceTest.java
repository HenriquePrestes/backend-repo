package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import plataformaSaude.model.Paciente;
import plataformaSaude.repository.PacienteRepository;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    PacienteRepository pacienteRepository;

    @InjectMocks
    PacienteService pacienteService;

    @Test
    void listarTodos_DeveChamarFindAllPageable() {
        var pageable = PageRequest.of(0, 10);
        Page<Paciente> pageMock = new PageImpl<>(List.of());

        when(pacienteRepository.findAll(pageable)).thenReturn(pageMock);
        Page<Paciente> result = pacienteService.listarTodos(pageable);

        assertThat(result).isEqualTo(pageMock);
        verify(pacienteRepository).findAll(pageable);
    }

    @Test
    void buscarPorEmail_DeveRetornarOptional() {
        Paciente paciente = new Paciente();
        when(pacienteRepository.findByEmail("test@test.com")).thenReturn(Optional.of(paciente));

        Optional<Paciente> result = pacienteService.buscarPorEmail("test@test.com");

        assertThat(result).contains(paciente);
        verify(pacienteRepository).findByEmail("test@test.com");
    }

    @Test
    void buscarPorId_DeveRetornarOptional() {
        Paciente paciente = new Paciente();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        Optional<Paciente> result = pacienteService.buscarPorId(1L);

        assertThat(result).contains(paciente);
        verify(pacienteRepository).findById(1L);
    }

    @Test
    void salvar_DeveChamarSave() {
        Paciente p = new Paciente();
        when(pacienteRepository.save(p)).thenReturn(p);

        Paciente result = pacienteService.salvar(p);

        assertThat(result).isEqualTo(p);
        verify(pacienteRepository).save(p);
    }

    @Test
    void deletar_DeveChamarDeleteById() {
        pacienteService.deletar(1L);

        verify(pacienteRepository).deleteById(1L);
    }
}
