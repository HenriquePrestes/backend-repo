package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import plataformaSaude.dto.HorarioDTO;
import plataformaSaude.dto.MedicoDTO;
import plataformaSaude.model.Especialidade;
import plataformaSaude.model.Horario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.EspecialidadeRepository;
import plataformaSaude.repository.HorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    HorarioRepository horarioRepository;

    @Mock
    EspecialidadeRepository especialidadeRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MedicoService medicoService;

    @Test
    void criarMedico_DeveCriarComSucesso() {

        MedicoDTO dto = new MedicoDTO();
        dto.setNomeCompleto("Fulano Teste");
        dto.setCpf("00011122233");
        dto.setEmail("fulano@test.com");
        dto.setSenha("123456");
        dto.setCelular("5199999999");
        dto.setDataNascimento(LocalDate.of(1990,1,1));
        dto.setCrm("CRM1234");
        dto.setEspecialidadeId(1L);
        dto.setFoto("http://img.com/foto.png");
        dto.setDuracaoConsultaMinutos(30);

        HorarioDTO h1 = new HorarioDTO();
        h1.setDescricao("Manhã");
        h1.setDiaSemana(DayOfWeek.MONDAY);
        h1.setHoraInicio(LocalTime.of(9,0));
        h1.setHoraFim(LocalTime.of(12,0));
        dto.setHorariosTrabalho(List.of(h1));

        // Mock da especialidade
        Especialidade especialidade = new Especialidade("Cardiologia");
        especialidade.setId(1L);
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));

        when(passwordEncoder.encode("123456")).thenReturn("senha_criptografada");

        when(medicoRepository.save(any(Medico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Medico medicoSalvo = medicoService.criarMedico(dto);

        assertThat(medicoSalvo.getNomeCompleto()).isEqualTo("Fulano Teste");
        assertThat(medicoSalvo.getSenha()).isEqualTo("senha_criptografada");
        assertThat(medicoSalvo.getHorariosTrabalho()).hasSize(1);
    }

    @Test
    void atualizarMedico_DeveAtualizarComSucesso() {
        Long medicoId = 1L;

        Medico medicoExistente = new Medico();
        medicoExistente.setId(medicoId);
        medicoExistente.setHorariosTrabalho(new ArrayList<>());

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medicoExistente));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));

        MedicoDTO dto = new MedicoDTO();
        dto.setNomeCompleto("Novo Nome");
        dto.setCpf("999");
        dto.setEmail("novo@mail.com");
        dto.setCelular("519999");
        dto.setDataNascimento(LocalDate.now());
        dto.setCrm("CRM99");
        dto.setEspecialidadeId(2L);
        dto.setFoto("urlfoto");
        dto.setDuracaoConsultaMinutos(45);

        HorarioDTO h1 = new HorarioDTO();
        h1.setDescricao("Tarde");
        h1.setDiaSemana(DayOfWeek.FRIDAY);
        h1.setHoraInicio(LocalTime.of(14,0));
        h1.setHoraFim(LocalTime.of(17,0));
        dto.setHorariosTrabalho(List.of(h1));

        // Mock da especialidade
        Especialidade especialidade = new Especialidade("Neurologia");
        especialidade.setId(2L);
        when(especialidadeRepository.findById(2L)).thenReturn(Optional.of(especialidade));

        Medico atualizado = medicoService.atualizarMedico(medicoId, dto);

        assertThat(atualizado.getNomeCompleto()).isEqualTo("Novo Nome");
        assertThat(atualizado.getDuracaoConsultaMinutos()).isEqualTo(45);
        assertThat(atualizado.getHorariosTrabalho()).hasSize(1);

        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void deletarMedico_DeveChamarDelete() {
        Long medicoId = 2L;
        when(medicoRepository.existsById(medicoId)).thenReturn(true);

        medicoService.deletarMedico(medicoId);

        verify(medicoRepository).deleteById(medicoId);
    }

    @Test
    void listarTodosDTO_DeveConverterComSucesso() {
        Medico m = new Medico();
        m.setId(10L);
        m.setNomeCompleto("Doctor X");
        m.setEmail("x@mail.com");
        m.setCelular("999");
        m.setCrm("C10");
        
        Especialidade ortopedia = new Especialidade("Ortopedia");
        ortopedia.setId(3L);
        m.setEspecialidade(ortopedia);
        
        m.setDuracaoConsultaMinutos(30);

        Horario h = new Horario();
        h.setId(1L);
        h.setDescricao("manhã");
        h.setDiaSemana(DayOfWeek.MONDAY);
        h.setHoraInicio(LocalTime.of(9,0));
        h.setHoraFim(LocalTime.of(12,0));
        h.setMedico(m);

        m.setHorariosTrabalho(List.of(h));

        when(medicoRepository.findAll()).thenReturn(List.of(m));

        var lista = medicoService.listarTodosDTO();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNomeCompleto()).isEqualTo("Doctor X");
        assertThat(lista.get(0).getHorariosTrabalho()).hasSize(1);
    }

    @Test
    void listarTodos_Pageable_DeveChamarFindAllSpec() {
        Pageable pageable = mock(Pageable.class);

        when(medicoRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable))
        ).thenReturn(Page.empty());

        medicoService.listarTodos(pageable, null, null, null, null);

        verify(medicoRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }
        @Test
    void buscarPorId_DeveChamarRepo() {
        medicoService.buscarPorId(99L);
        verify(medicoRepository).findById(99L);
    }

    @Test
    void listarTodosSemPageable_DeveChamarRepo() {
        medicoService.listarTodos();
        verify(medicoRepository).findAll();
    }
}
