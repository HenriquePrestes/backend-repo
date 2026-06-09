package plataformaSaude.specification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import plataformaSaude.TestConfig;
import plataformaSaude.model.Especialidade;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.EspecialidadeRepository;
import plataformaSaude.repository.MedicoRepository;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class MedicoSpecificationTest {

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    EspecialidadeRepository especialidadeRepository;

    @Test
    void deveFiltrarPorTermoBusca() {
        // Criar especialidades de teste
        Especialidade cardiologia = new Especialidade("Cardiologia");
        Especialidade dermatologia = new Especialidade("Dermatologia");
        especialidadeRepository.save(cardiologia);
        especialidadeRepository.save(dermatologia);

        Medico m1 = new Medico();
        m1.setNomeCompleto("João Cardoso");
        m1.setEspecialidade(cardiologia);
        m1.setCrm("123");
        m1.setCpf("00000000001");
        m1.setEmail("cardio@teste.com");
        m1.setSenha("abc123");
        medicoRepository.save(m1);

        Medico m2 = new Medico();
        m2.setNomeCompleto("Maria Silva");
        m2.setEspecialidade(dermatologia);
        m2.setCrm("456");
        m2.setCpf("00000000002");
        m2.setEmail("derma@teste.com");
        m2.setSenha("abc123");
        medicoRepository.save(m2);

        // when
        var spec = MedicoSpecification.withFilter(null, null, null, "cardio");
        List<Medico> resultado = medicoRepository.findAll(spec);

        // then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeCompleto()).isEqualTo("João Cardoso");
    }
}

