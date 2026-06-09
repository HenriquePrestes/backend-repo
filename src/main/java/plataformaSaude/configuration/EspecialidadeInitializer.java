package plataformaSaude.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import plataformaSaude.model.Especialidade;
import plataformaSaude.repository.EspecialidadeRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class EspecialidadeInitializer implements CommandLineRunner {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Só inicializa se não houver especialidades no banco
        if (especialidadeRepository.count() == 0) {
            List<String> especialidadesNomes = Arrays.asList(
                "Cardiologia",
                "Dermatologia", 
                "Endocrinologia",
                "Ginecologia",
                "Neurologia",
                "Oftalmologia",
                "Ortopedia",
                "Pediatria",
                "Psiquiatria",
                "Urologia",
                "Clínica Geral",
                "Anestesiologia",
                "Cirurgia Geral",
                "Gastroenterologia",
                "Hematologia",
                "Infectologia",
                "Nefrologia",
                "Oncologia",
                "Pneumologia",
                "Reumatologia"
            );

            for (String nome : especialidadesNomes) {
                if (!especialidadeRepository.existsByNome(nome)) {
                    Especialidade especialidade = new Especialidade(nome);
                    especialidadeRepository.save(especialidade);
                }
            }

            System.out.println("Especialidades inicializadas: " + especialidadeRepository.count() + " registros");
        } else {
            System.out.println("Especialidades já existem no banco: " + especialidadeRepository.count() + " registros");
        }
    }
}