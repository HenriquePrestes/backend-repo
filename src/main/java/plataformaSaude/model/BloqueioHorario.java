// src/main/java/plataformaSaude/model/BloqueioHorario.java
package plataformaSaude.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa um bloqueio na agenda do médico feito pelo Admin.
 * Ex: Férias, Almoço, Congresso, Pausa para café.
 */
@Entity
@Table(name = "bloqueios_horario")
public class BloqueioHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(nullable = false)
    private LocalDateTime dataInicio; // Data e hora de início do bloqueio

    @Column(nullable = false)
    private LocalDateTime dataFim; // Data e hora de fim do bloqueio

    private String motivo; // Ex: "Almoço", "Férias"

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}