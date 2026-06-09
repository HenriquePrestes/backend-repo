package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Paciente;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByEmail(String email);
    Optional<Paciente> findByCpf(String cpf);
}

