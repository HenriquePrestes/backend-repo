// src/main/java/plataformaSaude/repository/BloqueioHorarioRepository.java
package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plataformaSaude.model.BloqueioHorario;

import java.time.LocalDateTime;
import java.util.List;

public interface BloqueioHorarioRepository extends JpaRepository<BloqueioHorario, Long> {

    @Query("SELECT b FROM BloqueioHorario b WHERE b.medico.id = :medicoId " +
            "AND b.dataInicio < :fimDoDia " +
            "AND b.dataFim > :inicioDoDia")
    List<BloqueioHorario> findBlocksByMedicoAndDateRange(
            @Param("medicoId") Long medicoId,
            @Param("inicioDoDia") LocalDateTime inicioDoDia,
            @Param("fimDoDia") LocalDateTime fimDoDia);

    // +++ NOVA QUERY DE CONFLITO (Double Booking) +++
    /**
     * Verifica se existem bloqueios que se SOBREPÕEM
     * a um novo intervalo [inicio, fim].
     *
     * Lógica de Overlap: (StartA < EndB) AND (EndA > StartB)
     */
    @Query("SELECT b FROM BloqueioHorario b WHERE b.medico.id = :medicoId " +
            "AND b.dataInicio < :fim " +   // StartA < EndB
            "AND b.dataFim > :inicio") // EndA > StartB
    List<BloqueioHorario> findOverlappingBlocks(
            @Param("medicoId") Long medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    /**
     * Busca todos os bloqueios (passados e futuros) de um médico.
     */
    List<BloqueioHorario> findByMedicoId(Long medicoId);
}