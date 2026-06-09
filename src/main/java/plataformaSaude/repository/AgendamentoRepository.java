package plataformaSaude.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.model.Agendamento;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Método para listar todos com relacionamentos carregados
    @Query("SELECT a FROM Agendamento a " +
           "LEFT JOIN FETCH a.paciente " +
           "LEFT JOIN FETCH a.medico " +
           "ORDER BY a.dataConsultaInicio DESC")
    List<Agendamento> findAllWithRelationships();

    List<Agendamento> findByStatus(StatusAgendamento status);

    // Busca por ID
    List<Agendamento> findByMedicoId(Long medicoId);
    List<Agendamento> findByPacienteId(Long pacienteId);
    boolean existsByMedicoIdAndPacienteId(Long medicoId, Long pacienteId);

    // Busca por especialidade (usando JOIN)
    @Query("SELECT a FROM Agendamento a WHERE a.medico.especialidade = :especialidade")
    List<Agendamento> findByMedicoEspecialidade(@Param("especialidade") String especialidade);

    // Método de busca de futuros
    @Query("SELECT a FROM Agendamento a WHERE a.dataConsultaInicio >= :dataAtual ORDER BY a.dataConsultaInicio ASC")
    Page<Agendamento> findAgendamentosFuturos(
            @Param("dataAtual") LocalDateTime dataAtual,
            Pageable pageable
);

    /*MÉTODO QUE FALTAVA (Para o HorarioService)
      Busca agendamentos ativos para um médico em um DIA INTEIRO.
      Usado pelo HorarioService para saber quais slots já estão ocupados.*/
    @Query("SELECT a FROM Agendamento a WHERE a.medico.id = :medicoId " +
            "AND a.status NOT IN (plataformaSaude.Enum.StatusAgendamento.CANCELADO, plataformaSaude.Enum.StatusAgendamento.FALTOU) " +
            "AND a.dataConsultaInicio < :fimDoDia " +   // Início < 00:00 do dia SEGUINTE
            "AND a.dataConsultaFim > :inicioDoDia") // Fim > 00:00 do dia ATUAL
    List<Agendamento> findActiveAppointmentsByMedicoAndDateRange(
            @Param("medicoId") Long medicoId,
            @Param("inicioDoDia") LocalDateTime inicioDoDia,
            @Param("fimDoDia") LocalDateTime fimDoDia);


    // +++ NOVA QUERY DE CONFLITO (Para o AgendamentoService) +++
    /**
     * Busca agendamentos que se SOBREPÕEM a um novo intervalo [inicio, fim].
     * Usado pelo AgendamentoService para evitar double-booking.
     */
    @Query("SELECT a FROM Agendamento a WHERE a.medico.id = :medicoId " +
            "AND a.id != :idExcluir " +
            "AND a.status NOT IN (plataformaSaude.Enum.StatusAgendamento.CANCELADO, plataformaSaude.Enum.StatusAgendamento.FALTOU) " +
            "AND a.dataConsultaInicio < :fim " +   // StartA < EndB
            "AND a.dataConsultaFim > :inicio") // EndA > StartB
    List<Agendamento> findOverlappingAppointments(
            @Param("medicoId") Long medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("idExcluir") Long idExcluir);
    // +++ NOVO MÉTODO PARA O DASHBOARD +++
    /**
     * Conta de forma eficiente o número de agendamentos com um status específico.
     * @param status O status para contar (ex: AGENDADO)
     * @return O total (long) de agendamentos.
     */
    long countByStatus(StatusAgendamento status);
}
