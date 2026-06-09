package plataformaSaude.repository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import plataformaSaude.TestConfig;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.model.Agendamento;
import plataformaSaude.model.Especialidade;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestConfig.class)
class AgendamentoRepositoryTest {

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    EspecialidadeRepository especialidadeRepository;

    @Test
    void deveDetectarAgendamentoSobreposto() {
        // criar paciente
        Paciente p = new Paciente();
        p.setNomeCompleto("Joao");
        p.setCpf("11122233344");
        p.setEmail("joao@test.com");
        p.setSenha("123");
        p = pacienteRepository.save(p);

        // criar especialidade
        Especialidade cardiologia = new Especialidade("Cardiologia");
        cardiologia = especialidadeRepository.save(cardiologia);

        // criar médico
        Medico medico = new Medico();
        medico.setNomeCompleto("Dr Teste");
        medico.setCpf("99988877766");
        medico.setEmail("dr@test.com");
        medico.setSenha("123");
        medico.setEspecialidade(cardiologia);
        medico = medicoRepository.save(medico);

        // Datas futuras
        LocalDateTime inicioExistente = LocalDateTime.of(2050,1,1,9,0);
        LocalDateTime fimExistente = LocalDateTime.of(2050,1,1,10,0);

        // agendamento já existente
        Agendamento existente = new Agendamento();
        existente.setMedico(medico);
        existente.setPaciente(p);
        existente.setStatus(StatusAgendamento.AGENDADO);
        existente.setDataConsultaInicio(inicioExistente);
        existente.setDataConsultaFim(fimExistente);
        agendamentoRepository.save(existente);

        LocalDateTime novoInicio = LocalDateTime.of(2050,1,1,9,30);
        LocalDateTime novoFim = LocalDateTime.of(2050,1,1,9,45);

        List<Agendamento> overlaps =
                agendamentoRepository.findOverlappingAppointments(
                        medico.getId(),
                        novoInicio,
                        novoFim,
                        -1L // não excluir nenhum
                );

        assertThat(overlaps).hasSize(1); // detectou conflito
    }
}
