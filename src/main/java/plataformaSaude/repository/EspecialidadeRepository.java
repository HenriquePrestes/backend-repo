package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plataformaSaude.model.Especialidade;

import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    
    Optional<Especialidade> findByNome(String nome);
    
    boolean existsByNome(String nome);
}