package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.dto.DashboardDTO;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    AgendamentoRepository agendamentoRepository;

    @InjectMocks
    DashboardService dashboardService;

    @Test
    void getMetrics_DeveRetornarMetricasCorretas() {

        when(pacienteRepository.count()).thenReturn(10L);
        when(medicoRepository.count()).thenReturn(5L);
        when(agendamentoRepository.count()).thenReturn(20L);
        when(agendamentoRepository.countByStatus(StatusAgendamento.AGENDADO)).thenReturn(7L);

        DashboardDTO dto = dashboardService.getMetrics();

        assertThat(dto).isNotNull();
        assertThat(dto.getTotalPacientes()).isEqualTo(10);
        assertThat(dto.getTotalMedicos()).isEqualTo(5);
        assertThat(dto.getTotalAgendamentos()).isEqualTo(20);
        assertThat(dto.getAgendamentosPendentes()).isEqualTo(7);
    }
}
