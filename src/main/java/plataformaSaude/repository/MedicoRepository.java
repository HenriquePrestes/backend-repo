package plataformaSaude.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // NOVO IMPORT
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plataformaSaude.model.Medico;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long>, JpaSpecificationExecutor<Medico> {
    Optional<Medico> findByEmail(String email);
    Optional<Medico> findByCrm(String crm);
    @Query(value = "SELECT * FROM medicos m " +
            "WHERE to_tsvector('portuguese', " +
            "m.nome_completo || ' ' || m.especialidade || ' ' || m.crm) @@ to_tsquery('portuguese', :termoBusca)",
            nativeQuery = true)
    List<Medico> fullTextSearch(@Param("termoBusca") String termoBusca);
}