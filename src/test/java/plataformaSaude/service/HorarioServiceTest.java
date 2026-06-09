package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plataformaSaude.repository.HorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.model.Horario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock MedicoRepository medicoRepository;
    @Mock HorarioRepository horarioRepository;
    @Mock AgendamentoRepository agendamentoRepository;
    @Mock BloqueioHorarioRepository bloqueioHorarioRepository;

    @InjectMocks
    HorarioService service;

    @Test
    void listarTodos_deveRetornarPagina() {
        var pageable = PageRequest.of(0, 10);
        Page<Horario> pageMock = new PageImpl<>(List.of());
        when(horarioRepository.findAll(pageable)).thenReturn(pageMock);
        Page<Horario> result = service.listarTodos(pageable);
        assertThat(result).isSameAs(pageMock);
        verify(horarioRepository).findAll(pageable);
    }
    @Test
    void encontrarSlotsDisponiveis_DeveLancar_QuandoMedicoNaoExiste() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10);
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.encontrarSlotsDisponiveis(medicoId, data));
    }

    @Test
    void encontrarSlotsDisponiveis_DeveRetornarVazio_QuandoDuracaoInvalida() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10);

        var medico = new plataformaSaude.model.Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(0); // inválido

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        List<java.time.LocalTime> slots = service.encontrarSlotsDisponiveis(medicoId, data);
        assertThat(slots).isEmpty();
    }

    @Test
    void encontrarSlotsDisponiveis_DeveRetornarVazio_QuandoSemJanelas() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10);

        var medico = new plataformaSaude.model.Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(eq(medicoId), any()))
                .thenReturn(List.of()); // sem janelas

        List<LocalTime> slots = service.encontrarSlotsDisponiveis(medicoId, data);
        assertThat(slots).isEmpty();
    }

    @Test
    void encontrarSlotsDisponiveis_Sucesso_SemConflitos() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10); // segunda (tanto faz)
        int dur = 30;

        var medico = new plataformaSaude.model.Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(dur);

        var janela = new plataformaSaude.model.Horario();
        janela.setHoraInicio(LocalTime.of(9, 0));
        janela.setHoraFim(LocalTime.of(10, 0));

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(eq(medicoId), any()))
                .thenReturn(List.of(janela));

        // sem agendamentos/bloqueios
        when(agendamentoRepository.findActiveAppointmentsByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of());
        when(bloqueioHorarioRepository.findBlocksByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of());

        List<LocalTime> slots = service.encontrarSlotsDisponiveis(medicoId, data);

        assertThat(slots).containsExactly(LocalTime.of(9,0), LocalTime.of(9,30));
    }

    @Test
    void encontrarSlotsDisponiveis_ComConflitoDeAgendamento() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10);
        int dur = 30;

        var medico = new plataformaSaude.model.Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(dur);

        var janela = new plataformaSaude.model.Horario();
        janela.setHoraInicio(LocalTime.of(9, 0));
        janela.setHoraFim(LocalTime.of(10, 0));

        var ag = new plataformaSaude.model.Agendamento();
        ag.setDataConsultaInicio(LocalDateTime.of(data, LocalTime.of(9,30)));
        ag.setDataConsultaFim(LocalDateTime.of(data, LocalTime.of(10,0)));

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(eq(medicoId), any()))
                .thenReturn(List.of(janela));
        when(agendamentoRepository.findActiveAppointmentsByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of(ag));
        when(bloqueioHorarioRepository.findBlocksByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of());

        List<LocalTime> slots = service.encontrarSlotsDisponiveis(medicoId, data);

        assertThat(slots).containsExactly(LocalTime.of(9,0));
    }

    @Test
    void encontrarSlotsDisponiveis_ComConflitoDeBloqueio() {
        Long medicoId = 1L;
        LocalDate data = LocalDate.of(2025, 11, 10);
        int dur = 30;

        var medico = new plataformaSaude.model.Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(dur);

        var janela = new plataformaSaude.model.Horario();
        janela.setHoraInicio(LocalTime.of(9, 0));
        janela.setHoraFim(LocalTime.of(10, 0));

        var bl = new plataformaSaude.model.BloqueioHorario();
        bl.setDataInicio(LocalDateTime.of(data, LocalTime.of(9,0)));
        bl.setDataFim(LocalDateTime.of(data, LocalTime.of(9,30)));

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(horarioRepository.findByMedicoIdAndDiaSemana(eq(medicoId), any()))
                .thenReturn(List.of(janela));
        when(agendamentoRepository.findActiveAppointmentsByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of());
        when(bloqueioHorarioRepository.findBlocksByMedicoAndDateRange(eq(medicoId), any(), any()))
                .thenReturn(List.of(bl));

        List<LocalTime> slots = service.encontrarSlotsDisponiveis(medicoId, data);

        assertThat(slots).containsExactly(LocalTime.of(9,30));
    }
}
