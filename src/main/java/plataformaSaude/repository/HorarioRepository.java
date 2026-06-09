// src/main/java/plataformaSaude/repository/HorarioRepository.java
package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plataformaSaude.model.Horario;
import java.time.DayOfWeek; // IMPORTAR
import java.util.List; // IMPORTAR

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * Busca todas as janelas de trabalho (ex: 09:00-12:00 e 14:00-18:00)
     * para um médico específico em um dia da semana.
     */
    List<Horario> findByMedicoIdAndDiaSemana(Long medicoId, DayOfWeek diaSemana);
}