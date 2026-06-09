// src/main/java/plataformaSaude/dto/AgendamentoDTO.java
package plataformaSaude.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import plataformaSaude.Enum.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO para CRIAR ou ATUALIZAR um Agendamento.
 * O frontend envia apenas os IDs e a data/hora de início (o slot clicado).
 */
public class AgendamentoDTO {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "ID do médico é obrigatório")
    private Long medicoId;

    @NotNull(message = "Data da consulta é obrigatória")
    // Removido @Future temporariamente para permitir agendamentos no mesmo dia
    // @Future(message = "Data da consulta deve ser no futuro")
    private LocalDateTime dataConsultaInicio; // Ex: 2025-11-20T09:15:00

    private String observacoes;

    // Status é opcional, usado mais para atualizações
    private StatusAgendamento status;

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public LocalDateTime getDataConsultaInicio() { return dataConsultaInicio; }
    public void setDataConsultaInicio(LocalDateTime dataConsultaInicio) { this.dataConsultaInicio = dataConsultaInicio; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    //
    // OS CAMPOS ANTIGOS (nomePaciente, email, telefone, especialidade, nomeMedico)
    // NÃO SÃO MAIS NECESSÁRIOS AQUI.
    //
}