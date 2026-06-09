package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plataformaSaude.dto.AgendamentoDTO;
import plataformaSaude.model.Agendamento;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import plataformaSaude.Enum.StatusAgendamento;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    AgendamentoRepository agendamentoRepository;

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    BloqueioHorarioRepository bloqueioHorarioRepository;

    @InjectMocks
    AgendamentoService agendamentoService;

    @Test
    void criarAgendamento_DeveCriarComSucesso_QuandoNaoHaConflitos() {

        // arrange
        Long pacienteId = 1L;
        Long medicoId = 2L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(inicio);
        dto.setObservacoes("teste");

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        // mocks
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // sem conflitos
        lenient().when(agendamentoRepository.findOverlappingAppointments(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        lenient().when(bloqueioHorarioRepository.findOverlappingBlocks(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        // o save deve retornar o próprio objeto
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        Agendamento resultado = agendamentoService.criarAgendamento(dto);

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPaciente()).isEqualTo(paciente);
        assertThat(resultado.getMedico()).isEqualTo(medico);
        assertThat(resultado.getDataConsultaInicio()).isEqualTo(inicio);
        assertThat(resultado.getDataConsultaFim()).isEqualTo(inicio.plusMinutes(30));

        verify(agendamentoRepository, times(1)).save(any());
    }
    @Test
    void criarAgendamento_DeveLancarException_QuandoHaConflito() {

        // arrange
        Long pacienteId = 1L;
        Long medicoId = 2L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(inicio);

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // simula conflito: retorno não vazio
        ArrayList<Agendamento> conflito = new ArrayList<>();
        conflito.add(new Agendamento());

        lenient().when(agendamentoRepository.findOverlappingAppointments(any(), any(), any(), any()))
                .thenReturn(conflito);

        // act + assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> agendamentoService.criarAgendamento(dto)
        );
    }
    @Test
    void criarAgendamento_DeveLancarException_QuandoDataNoPassado() {

        // arrange
        Long pacienteId = 1L;
        Long medicoId = 2L;
        LocalDateTime inicioPassado = LocalDateTime.now().minusDays(1); // ontem

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(inicioPassado);

        // act + assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> agendamentoService.criarAgendamento(dto)
        );
    }
    @Test
    void criarAgendamento_DeveLancarException_QuandoMedicoNaoExiste() {

        // arrange
        Long pacienteId = 1L;
        Long medicoId = 999L;

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(LocalDateTime.now().plusDays(1));

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.empty());

        // act + assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> agendamentoService.criarAgendamento(dto)
        );
    }
    @Test
    void criarAgendamento_DeveLancarException_QuandoMedicoTemDuracaoInvalida() {

        // arrange
        Long pacienteId = 1L;
        Long medicoId = 2L;

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(LocalDateTime.now().plusDays(1));

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(0); // inválido !!

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // act + assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> agendamentoService.criarAgendamento(dto)
        );
    }
    @Test
    void listarTodos_DeveChamarFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<Agendamento> page = mock(Page.class);

        when(agendamentoRepository.findAll(pageable)).thenReturn(page);

        Page<Agendamento> result = agendamentoService.listarTodos(pageable);

        assertThat(result).isEqualTo(page);
        verify(agendamentoRepository, times(1)).findAll(pageable);
    }
    @Test
    void listarFuturos_DeveChamarFindAgendamentosFuturos() {
        Pageable pageable = mock(Pageable.class);
        Page<Agendamento> page = mock(Page.class);

        when(agendamentoRepository.findAgendamentosFuturos(any(), eq(pageable))).thenReturn(page);

        Page<Agendamento> result = agendamentoService.listarFuturos(pageable);

        assertThat(result).isEqualTo(page);
        verify(agendamentoRepository, times(1)).findAgendamentosFuturos(any(), eq(pageable));
    }
    @Test
    void atualizarStatus_DeveAtualizarStatus() {
        Long id = 1L;
        Agendamento a = new Agendamento();
        a.setId(id);
        a.setStatus(StatusAgendamento.AGENDADO);

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(a));
        when(agendamentoRepository.save(a)).thenReturn(a);

        Agendamento result = agendamentoService.atualizarStatus(id, StatusAgendamento.CANCELADO);

        assertThat(result.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
        verify(agendamentoRepository).save(a);
    }
    @Test
    void buscarPorPaciente_DeveChamarRepositorio() {
        Long pacienteId = 7L;
        agendamentoService.buscarPorPaciente(pacienteId);
        verify(agendamentoRepository).findByPacienteId(pacienteId);
    }
    @Test
    void buscarPorMedico_DeveChamarRepositorio() {
        Long medicoId = 7L;
        agendamentoService.buscarPorMedico(medicoId);
        verify(agendamentoRepository).findByMedicoId(medicoId);
    }
    @Test
    void buscarPorEspecialidade_DeveChamarRepositorio() {
        String espec = "Ortopedia";
        agendamentoService.buscarPorEspecialidade(espec);
        verify(agendamentoRepository).findByMedicoEspecialidade(espec);
    }
    @Test
    void buscarPorStatus_DeveChamarRepositorio() {
        agendamentoService.buscarPorStatus(StatusAgendamento.AGENDADO);
        verify(agendamentoRepository).findByStatus(StatusAgendamento.AGENDADO);
    }
    @Test
    void atualizarAgendamento_DeveAtualizarComSucesso_QuandoNaoHaConflito() {

        Long id = 10L;
        Long pacienteId = 1L;
        Long medicoId = 2L;

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(LocalDateTime.now().plusDays(2));
        dto.setObservacoes("teste");

        Agendamento original = new Agendamento();
        original.setId(id);

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(original));
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // sem conflitos
        lenient().when(agendamentoRepository.findOverlappingAppointments(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        lenient().when(bloqueioHorarioRepository.findOverlappingBlocks(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento updated = agendamentoService.atualizarAgendamento(id, dto);

        assertThat(updated.getPaciente()).isEqualTo(paciente);
        assertThat(updated.getMedico()).isEqualTo(medico);
    }
    @Test
    void atualizarAgendamento_DeveLancarException_QuandoHaConflito() {

        Long id = 10L;
        Long pacienteId = 1L;
        Long medicoId = 2L;

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(LocalDateTime.now().plusDays(2));

        Agendamento original = new Agendamento();
        original.setId(id);

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(original));
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // conflito
        ArrayList<Agendamento> conflito = new ArrayList<>();
        conflito.add(new Agendamento());

        when(agendamentoRepository.findOverlappingAppointments(any(), any(), any(), any()))
                .thenReturn(conflito);

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> agendamentoService.atualizarAgendamento(id, dto)
        );
    }
    @Test
    void criarAgendamento_DeveSanitizarObservacoes() {

        Long pacienteId = 1L;
        Long medicoId = 2L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);

        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setPacienteId(pacienteId);
        dto.setMedicoId(medicoId);
        dto.setDataConsultaInicio(inicio);
        dto.setObservacoes("<script>alert('xss')</script> texto");

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Medico medico = new Medico();
        medico.setId(medicoId);
        medico.setDuracaoConsultaMinutos(30);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        // sem conflito
        lenient().when(agendamentoRepository.findOverlappingAppointments(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        lenient().when(bloqueioHorarioRepository.findOverlappingBlocks(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento result = agendamentoService.criarAgendamento(dto);
        //sanitizer remove html/script
        assertThat(result.getObservacoes()).doesNotContain("<script>");
    }
}
