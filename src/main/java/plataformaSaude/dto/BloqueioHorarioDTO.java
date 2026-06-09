// src/main/java/plataformaSaude/dto/BloqueioHorarioDTO.java
package plataformaSaude.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO para o Admin criar ou atualizar um Bloqueio de Horário.
 */
public class BloqueioHorarioDTO {

    @NotNull(message = "ID do médico é obrigatório")
    private Long medicoId;

    @NotNull(message = "Data de início é obrigatória")
    @FutureOrPresent(message = "Data de início deve ser no presente ou futuro")
    private LocalDateTime dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    @FutureOrPresent(message = "Data de fim deve ser no presente ou futuro")
    private LocalDateTime dataFim;

    private String motivo; // Ex: "Férias", "Congresso", "Almoço"

    // Getters e Setters
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}