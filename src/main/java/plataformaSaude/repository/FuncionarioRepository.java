package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}

