package plataformaSaude.dto;

import plataformaSaude.model.Agendamento;
import plataformaSaude.Enum.StatusAgendamento;
import java.time.LocalDateTime;

public class AgendamentoResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;
    private String medicoEspecialidade;
    private String medicoCrm;
    private LocalDateTime dataConsultaInicio;
    private String observacoes;
    private StatusAgendamento status;

    // Construtores
    public AgendamentoResponseDTO() {}

    public AgendamentoResponseDTO(Agendamento agendamento) {
        this.id = agendamento.getId();
        this.pacienteId = agendamento.getPaciente().getId();
        this.pacienteNome = agendamento.getPaciente().getNomeCompleto();
        this.medicoId = agendamento.getMedico().getId();
        this.medicoNome = agendamento.getMedico().getNomeCompleto();
        this.medicoEspecialidade = agendamento.getMedico().getEspecialidade() != null ? 
                                    agendamento.getMedico().getEspecialidade().getNome() : "";
        this.medicoCrm = agendamento.getMedico().getCrm();
        this.dataConsultaInicio = agendamento.getDataConsultaInicio();
        this.observacoes = agendamento.getObservacoes();
        this.status = agendamento.getStatus();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public void setMedicoNome(String medicoNome) {
        this.medicoNome = medicoNome;
    }

    public String getMedicoEspecialidade() {
        return medicoEspecialidade;
    }

    public void setMedicoEspecialidade(String medicoEspecialidade) {
        this.medicoEspecialidade = medicoEspecialidade;
    }

    public String getMedicoCrm() {
        return medicoCrm;
    }

    public void setMedicoCrm(String medicoCrm) {
        this.medicoCrm = medicoCrm;
    }

    public LocalDateTime getDataConsultaInicio() {
        return dataConsultaInicio;
    }

    public void setDataConsultaInicio(LocalDateTime dataConsultaInicio) {
        this.dataConsultaInicio = dataConsultaInicio;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
}