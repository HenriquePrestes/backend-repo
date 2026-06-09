package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plataformaSaude.model.HistoricoBusca;

/* Repositório para a entidade HistoricoBusca. */
@Repository
public interface HistoricoBuscaRepository extends JpaRepository<HistoricoBusca, Long> {

    // (Opcional: Adicionar métodos de busca futuros)
    // Ex: List<HistoricoBusca> findByUsuarioIdOrderByDataBuscaDesc(Long usuarioId);
}